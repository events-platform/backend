package com.example.eventsplatformbackend.service.post;

import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.domain.dto.request.PostCreationDto;
import com.example.eventsplatformbackend.adapter.repository.PostRepository;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDto;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.domain.entity.Post;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.common.exception.InvalidDateException;
import com.example.eventsplatformbackend.common.exception.PostAlreadyExistsException;
import com.example.eventsplatformbackend.common.exception.PostNotFoundException;
import com.example.eventsplatformbackend.common.mapper.PostMapper;
import com.example.eventsplatformbackend.common.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Работает с мероприятиями
 */
@Service
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final PostFileService postFileService;

    public PostService(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper, UserMapper userMapper, PostFileService postFileService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
        this.userMapper = userMapper;
        this.postFileService = postFileService;
    }

    @Transactional(propagation= Propagation.REQUIRED)
    public ResponseEntity<String> savePost(PostCreationDto postCreationDto, Principal principal) throws PostAlreadyExistsException, InvalidDateException {
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
        if(postCreationDto.getFile() != null){
            String link = postFileService.saveAndGetLink(postCreationDto.getFile());
            post.setImage(link);
        }

        User user = userRepository.getUserByUsername(principal.getName());
        user.getCreatedPosts().add(post);
        post.setOwner(user);

        postRepository.save(post);
        userRepository.save(user);
        return ResponseEntity
                .ok()
                .header("Content-Type", "text/html; charset=utf-8")
                .body("Мероприятие успешно сохранено");
    }
    @Transactional
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        List<PostResponseDto> postResponseDtos = postRepository.findAll().stream()
                .map(postMapper::postDtoFromPost)
                .toList();
        return ResponseEntity.ok(postResponseDtos);
    }

    public Optional<Post> findById(Long postId) {
        return postRepository.findById(postId);
    }

    public ResponseEntity<PostResponseDto> getPostById(Long postId) throws PostNotFoundException {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Мероприятие с таким id не найдено"));
        return ResponseEntity.ok(postMapper.postDtoFromPost(post));
    }

    public ResponseEntity<List<UserDto>> getPostSubscribers(Long postId) throws PostNotFoundException {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Мероприятие с таким id не найдено"));
        List<UserDto> users = userRepository.getUsersBySubscribedPosts(post).stream().
                map(userMapper::userToUserDto)
                .toList();

        return ResponseEntity.ok(users);
    }

    public ResponseEntity<PageImpl<PostResponseDto>> getPostsPagination(Pageable pageable) {
        Page<Post> postsPage = postRepository.findAll(pageable);
        PageImpl<PostResponseDto> postsPageDto = new PageImpl<>(
                postsPage.stream()
                    .map(postMapper::postDtoFromPost)
                    .toList(),
                pageable,
                postsPage.getTotalElements());
        return ResponseEntity.ok(postsPageDto);
    }
}
