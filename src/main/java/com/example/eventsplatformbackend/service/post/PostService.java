package com.example.eventsplatformbackend.service.post;

import com.example.eventsplatformbackend.common.exception.*;
import com.example.eventsplatformbackend.domain.dto.request.PostCreationDto;
import com.example.eventsplatformbackend.adapter.repository.PostRepository;
import com.example.eventsplatformbackend.domain.dto.request.PostEditDto;
import com.example.eventsplatformbackend.domain.dto.request.PostSearchDto;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDtoImpl;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.domain.entity.Post;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.common.mapper.PostMapper;
import com.example.eventsplatformbackend.common.mapper.UserMapper;
import com.example.eventsplatformbackend.domain.enumeration.EFormat;
import com.example.eventsplatformbackend.domain.enumeration.ERole;
import com.example.eventsplatformbackend.domain.enumeration.EType;
import com.example.eventsplatformbackend.service.user.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Работает с мероприятиями пользователей
 * Может добавлять/удалять мероприятие из избранных, регистрировать/удалять пользователя как участника мероприятия
 */
@Service
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final PostFileService postFileService;

    public PostService(PostRepository postRepository, UserService userService,
                       PostMapper postMapper, UserMapper userMapper, PostFileService postFileService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.postMapper = postMapper;
        this.userMapper = userMapper;
        this.postFileService = postFileService;
    }

    public String savePost(PostCreationDto postCreationDto, MultipartFile file, String username) {

        Post post = postMapper.postCreationDtoToPost(postCreationDto);

        validatePostDate(post);
        if (postRepository.existsPostByBeginDateAndName(post.getBeginDate(), post.getName())) {
            throw new PostAlreadyExistsException("Мероприятие с таким названием и датой начала уже существует");
        }
        if (file != null) {
            String link = postFileService.saveAndGetLink(file);
            post.setImage(link);
        }

        User user = userService.getUserByUsername(username);
        user.getCreatedPosts().add(post);
        post.setOwner(user);

        postRepository.save(post);
        userService.saveUser(user);
        return "Мероприятие успешно сохранено";
    }

    public List<PostResponseDtoImpl> getAllPosts() {

        return postRepository.findAll().stream()
                .map(postMapper::postDtoFromPost)
                .toList();
    }

    public Optional<Post> findById(Long postId) {
        return postRepository.findById(postId);
    }

    public PostResponseDtoImpl getPostById(Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Мероприятие с таким id не найдено"));

        return postMapper.postDtoFromPost(post);
    }

    public List<UserDto> getPostSubscribers(Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Мероприятие с таким id не найдено"));

        return userService.getPostSubscribers(post).stream()
                .map(userMapper::userToUserDto)
                .toList();
    }

    // TODO: refactor this!
    @SneakyThrows
    public Page<PostResponseDtoImpl> getPostsPaginationWithFilters(PostSearchDto searchDto) {

        var parsedTypes = parseEventTypes(searchDto.getType());
        var parsedFormats = parseEventFormats(searchDto.getFormat());

        LocalDateTime endedPostsFilter = null;
        if (Boolean.FALSE.equals(searchDto.getShowEnded())) {
            endedPostsFilter = LocalDateTime.now();
        }

        return postRepository.findPostsByFilters(
                        searchDto.getBeginDate(),
                        searchDto.getEndDate(),
                        searchDto.getOrganizer(),
                        parsedTypes,
                        parsedFormats,
                        endedPostsFilter,
                        searchDto.getSearchQuery(),
                        searchDto.getPageable())
                .map(postMapper::postDtoFromPost);
    }

    private List<EFormat> parseEventFormats(List<String> rawFormats) {

        if (rawFormats != null) {
            return rawFormats.stream()
                    .map(EFormat::findByKey)
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            return new ArrayList<>();
        }
    }

    private List<EType> parseEventTypes(List<String> rawTypes) {

        if (rawTypes != null) {
            return rawTypes.stream()
                    .map(EType::findByKey)
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            return new ArrayList<>();
        }
    }

    public String deletePost(Long postId, String username) {

        Post postToDelete = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Мероприятие с таким id не найдено"));
        User user = userService.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователя с именем %s не существует", username)));

        if (postToDelete.getOwner().getId().equals(user.getId()) || user.getRole().equals(ERole.ROLE_ADMIN)) {
            postRepository.deletePostFromAllTables(postToDelete.getId());
            return "Мероприятие успешно удалено";
        }
        throw new PostAccessDeniedException("Вы не можете удалять чужие мероприятия");
    }

    public PostResponseDtoImpl editPost(PostEditDto postEditDto, MultipartFile file, String username) {

        Post post = postRepository.findById(postEditDto.getPostId()).orElseThrow(() ->
                new PostNotFoundException("Мероприятие с таким id не найдено"));
        User user = userService.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователя с именем %s не существует", username)));

        if (!post.getOwner().getId().equals(user.getId()) && !user.getRole().equals(ERole.ROLE_ADMIN)) {
            throw new PostAccessDeniedException("Вы не можете редактировать чужие мероприятия");
        }
        post = postMapper.postFromPostEditDto(postEditDto, post);

        validatePostDate(post);
        if (postRepository.findPostsByBeginDateAndName(post.getBeginDate(), post.getName()).size() > 1) {
            throw new PostAlreadyExistsException("Мероприятие с таким названием и датой начала уже существует");
        }
        if (file != null) {
            String link = postFileService.saveAndGetLink(file);
            post.setImage(link);
        }
        return postMapper.postDtoFromPost(post);
    }

    private void validatePostDate(Post post) {

        if (post.getBeginDate().isAfter(post.getEndDate())
                || post.getBeginDate().isEqual(post.getEndDate())) {
            throw new InvalidDateException("Мероприятие не может кончаться раньше, чем начнется");
        }
        if (post.getBeginDate().isBefore(LocalDate.now().atStartOfDay())) {
            throw new InvalidDateException("Мероприятие не может начинаться раньше сегодняшнего дня");
        }
    }
}
