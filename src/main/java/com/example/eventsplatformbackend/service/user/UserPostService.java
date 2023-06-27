package com.example.eventsplatformbackend.service.user;

import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.domain.dto.request.PostIdDto;
import com.example.eventsplatformbackend.domain.dto.response.PersonalizedPostResponseDtoImpl;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDtoImpl;
import com.example.eventsplatformbackend.domain.entity.Post;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.common.exception.PostNotFoundException;
import com.example.eventsplatformbackend.common.exception.UserNotFoundException;
import com.example.eventsplatformbackend.common.mapper.PostMapper;
import com.example.eventsplatformbackend.service.post.PostService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * Работает с мероприятиями пользователей
 */
@Service
@Transactional
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
    public Page<PostResponseDtoImpl> getUserCreatedPostsPagination(String username, Pageable pageable) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));

        return userRepository.findAllUserCreatedPosts(user.getId(), pageable)
                .map(postMapper::postDtoFromPost);
    }
    public String addPostToFavorites(PostIdDto postIdDto, String username) {
        Post post = postService.findById(postIdDto.getPostId()).orElseThrow(() ->
                new PostNotFoundException(POST_NOT_FOUND));
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));

        user.getFavoritePosts().add(post);

        log.info("{} added {} to favorites", username, postIdDto.getPostId());
        return "Мероприятие добавлено в избранное";
    }
    public String removePostFromFavorites(PostIdDto postIdDto, String username) {
        Post post = postService.findById(postIdDto.getPostId()).orElseThrow(() ->
                new PostNotFoundException(POST_NOT_FOUND));
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));

        user.getFavoritePosts().remove(post);
        log.info("{} removed {} from favorites", username, postIdDto.getPostId());
        return "Мероприятие удалено из избранного";
    }
    public Page<PostResponseDtoImpl> getFavoritePosts(String username, Pageable pageable) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));

        return userRepository.findAllUserFavoritePosts(user.getId(), pageable)
                .map(postMapper::postDtoFromPost);
    }
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
    public Page<PostResponseDtoImpl> getUserSubscriptions(String username, Pageable pageable) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));

        return userRepository.findAllUserSubscribedPosts(user.getId(), pageable)
                .map(postMapper::postDtoFromPost);
    }
    public String unsubscribeFromPost(PostIdDto postIdDto, String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));
        Post toUnsubscribe = postService.findById(postIdDto.getPostId()).orElseThrow(() ->
                new PostNotFoundException(POST_NOT_FOUND));

        user.getSubscribedPosts().remove(toUnsubscribe);
        log.info("{} unsubscribed from {}", username, postIdDto.getPostId());
        return "Вы больше не подписаны на это мероприятие";
    }
    public PersonalizedPostResponseDtoImpl getPersonalizedPost(Long postId, Principal principal) {
        Post post = postService.findById(postId).orElseThrow(() ->
                new PostNotFoundException(POST_NOT_FOUND));
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() ->
                new UserNotFoundException(USER_NOT_FOUND));

        PersonalizedPostResponseDtoImpl responseDto = postMapper.personalizedPostDtoFromPost(post);

        responseDto.setIsSubscribed(user.getSubscribedPosts().contains(post));
        responseDto.setIsFavorite(user.getFavoritePosts().contains(post));
        return responseDto;
    }
}
