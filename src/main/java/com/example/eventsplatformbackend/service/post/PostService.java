package com.example.eventsplatformbackend.service.post;

import com.example.eventsplatformbackend.common.exception.*;
import com.example.eventsplatformbackend.domain.dto.request.PostCreationDto;
import com.example.eventsplatformbackend.adapter.repository.PostRepository;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDto;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.domain.entity.Post;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.common.mapper.PostMapper;
import com.example.eventsplatformbackend.common.mapper.UserMapper;
import com.example.eventsplatformbackend.domain.enumeration.EFormat;
import com.example.eventsplatformbackend.domain.enumeration.ERole;
import com.example.eventsplatformbackend.domain.enumeration.EType;
import com.example.eventsplatformbackend.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Работает с мероприятиями пользователей
 * Может добавлять/удалять мероприятие из избранных, регистрировать/удалять пользователя как участника мероприятия
 */
@Service
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

    @Transactional(propagation= Propagation.REQUIRED)
    public String savePost(PostCreationDto postCreationDto, MultipartFile file, Principal principal) {
        Post post = postMapper.postCreationDtoToPost(postCreationDto);

        if (postRepository.existsPostByBeginDateAndName(post.getBeginDate(), post.getName())){
            throw new PostAlreadyExistsException("Мероприятие с таким названием и датой начала уже существует");
        }
        if (post.getBeginDate().isAfter(post.getEndDate())
                || post.getBeginDate().isEqual(post.getEndDate())){
            throw new InvalidDateException("Мероприятие не может кончаться раньше, чем начнется");
        }
        if(post.getBeginDate().isBefore(LocalDate.now().atStartOfDay())){
            throw new InvalidDateException("Мероприятие не может начинаться раньше сегодняшнего дня");
        }
        if(file != null){
            String link = postFileService.saveAndGetLink(file);
            post.setImage(link);
        }

        User user = userService.getUserByUsername(principal.getName());
        user.getCreatedPosts().add(post);
        post.setOwner(user);

        postRepository.save(post);
        userService.saveUser(user);
        return "Мероприятие успешно сохранено";
    }
    @Transactional
    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(postMapper::postDtoFromPost)
                .toList();
    }

    public Optional<Post> findById(Long postId) {
        return postRepository.findById(postId);
    }

    public PostResponseDto getPostById(Long postId) {
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

    public Page<PostResponseDto> getPostsPaginationWithFilters(
            LocalDateTime beginDateFilter,
            LocalDateTime endDateFilter,
            List<String> organizers,
            List<String> types,
            List<String> formats,
            Boolean showEndedPosts,
            String searchQuery,
            Pageable pageable) throws EventTypeNotExistsException{

        List<EType> parsedTypes = new ArrayList<>();
        if (types != null) {
            types.forEach(rawType -> {
                EType type = EType.findByKey(rawType);
                if (type != null) {
                    parsedTypes.add(type);
                } else {
                    throw new EventTypeNotExistsException(String.format("Event type '%s' is not present", rawType));
                }
            });
        }
        List<EFormat> parsedFormats = new ArrayList<>();
        if (formats != null) {
            formats.forEach(rawFormat -> {
                EFormat format = EFormat.findByKey(rawFormat);
                if (format != null) {
                    parsedFormats.add(format);
                } else {
                    throw new EventFormatNotExistsException(String.format("Event format '%s' is not present", rawFormat));
                }
            });
        }
        LocalDateTime endedPostsFilter = null;
        if (Boolean.FALSE.equals(showEndedPosts)){
            endedPostsFilter = LocalDateTime.now();
        }
        return postRepository.findPostsByFilters(
                beginDateFilter, endDateFilter, organizers,
                parsedTypes, parsedFormats, endedPostsFilter, searchQuery, pageable)
                .map(postMapper::postDtoFromPost);
    }
    @Transactional
    public String deletePost(Long postId, String username) {
        Post postToDelete = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Мероприятие с таким id не найдено"));
        User user = userService.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователя с именем %s не существует", username)));

        if (postToDelete.getOwner().getId().equals(user.getId()) || user.getRole().equals(ERole.ROLE_ADMIN)){
            postRepository.deletePostFromAllTables(postToDelete.getId());
            return "Мероприятие успешно удалено";
        }
        throw new PostAccessDeniedException("Вы не можете удалять чужие мероприятия");
    }
}
