package com.example.eventsplatformbackend.service.user;

import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.domain.dto.request.PostIdDto;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDto;
import com.example.eventsplatformbackend.domain.entity.Post;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.common.exception.PostNotFoundException;
import com.example.eventsplatformbackend.common.exception.UserNotFoundException;
import com.example.eventsplatformbackend.common.mapper.PostMapper;
import com.example.eventsplatformbackend.service.post.PostService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Работает с мероприятиями пользователей
 */
@Service
@Slf4j
public class UserPostService {
    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String POST_NOT_FOUND = "Мероприятие не найдено";
    private final UserRepository userRepository;
    private final PostService postService;
    private final PostMapper postMapper;

    public UserPostService(UserRepository userRepository, PostService postService, PostMapper postMapper) {
        this.userRepository = userRepository;
        this.postService = postService;
        this.postMapper = postMapper;
    }

    @Transactional
    public List<PostResponseDto> getUserCreatedPosts(String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));

        return user.getCreatedPosts().stream()
                .map(postMapper::postDtoFromPost)
                .toList();
    }
    @Transactional
    public String addPostToFavorites(PostIdDto postIdDto, String username) {
        Post post = postService.findById(postIdDto.getPostId()).orElseThrow(() ->
                new PostNotFoundException(POST_NOT_FOUND));
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));

        if(!user.getFavoritePosts().contains(post)){
            user.getFavoritePosts().add(post);
            userRepository.save(user);
        }
        log.info("{} added {} to favorites", username, postIdDto.getPostId());
        return "Мероприятие добавлено в избранное";
    }
    @Transactional
    public String removePostFromFavorites(PostIdDto postIdDto, String username) {
        Post post = postService.findById(postIdDto.getPostId()).orElseThrow(() ->
                new PostNotFoundException(POST_NOT_FOUND));
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));

        user.getFavoritePosts().remove(post);
        userRepository.save(user);
        log.info("{} removed {} from favorites", username, postIdDto.getPostId());
        return "Мероприятие удалено из избранного";
    }
    @Transactional
    public List<PostResponseDto> getFavoritePosts(String username) {
        log.info(username);
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));

        return user.getFavoritePosts().stream()
                .map(postMapper::postDtoFromPost)
                .toList();
    }
    @Transactional
    public ResponseEntity<String> subscribeToPost(PostIdDto postIdDto, String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));
        Post post = postService.findById(postIdDto.getPostId()).orElseThrow(() ->
                new PostNotFoundException(POST_NOT_FOUND));

        if (user.getSubscribedPosts().contains(post)){
            return ResponseEntity
                    .ok()
                    .header("Content-Type", "text/html; charset=utf-8")
                    .body("Вы уже подписаны на это мероприятие");
        }

        if (userRepository.countUsersBySubscribedPosts(post) < post.getRegistrationLimit()){
            user.getSubscribedPosts().add(post);
            userRepository.save(user);
            log.info("{} subscribed to {}", username, postIdDto.getPostId());

            return ResponseEntity
                    .ok()
                    .header("Content-Type", "text/html; charset=utf-8")
                    .body("Вы успешно подписались на мероприятие");
        } else {
            return ResponseEntity
                    .badRequest()
                    .header("Content-Type", "text/html; charset=utf-8")
                    .body("Невозможно записаться, на это мероприятие больше нет свободных мест");
        }
    }
    @Transactional
    public List<PostResponseDto> getUserSubscriptions(String username) throws UserNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));

        return user.getSubscribedPosts().stream()
                .map(postMapper::postDtoFromPost)
                .toList();
    }
    @Transactional
    public String unsubscribeFromPost(PostIdDto postIdDto, String username)
            throws PostNotFoundException, UserNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));
        Post toUnsubscribe = postService.findById(postIdDto.getPostId()).orElseThrow(() ->
                new PostNotFoundException(POST_NOT_FOUND));

        user.getSubscribedPosts().remove(toUnsubscribe);
        userRepository.save(user);
        log.info("{} unsubscribed from {}", username, postIdDto.getPostId());
        return "Вы больше не подписаны на это мероприятие";
    }
}
