package com.example.eventsplatformbackend.service.user;

import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDto;
import com.example.eventsplatformbackend.domain.entity.Post;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.exception.PostNotFoundException;
import com.example.eventsplatformbackend.mapper.PostMapper;
import com.example.eventsplatformbackend.service.post.PostService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Работает с мероприятиями пользователей
 */
@Service
@Slf4j
public class UserPostService {
    private final UserRepository userRepository;
    private final PostService postService;
    private final PostMapper postMapper;

    public UserPostService(UserRepository userRepository, PostService postService, PostMapper postMapper) {
        this.userRepository = userRepository;
        this.postService = postService;
        this.postMapper = postMapper;
    }

    @Transactional
    public ResponseEntity<List<PostResponseDto>> getUserCreatedPosts(String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Пользователя с таким именем не существует"));

        List<PostResponseDto> posts = user.getCreatedPosts().stream()
                .map(postMapper::postDtoFromPost)
                .toList();
        return ResponseEntity.ok(posts);
    }
    @Transactional
    public ResponseEntity<String> addPostToFavorites(Long postId, String username) throws PostNotFoundException {
        Post post = postService.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Мероприятие с таким id не найдено"));
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Пользователя с таким именем не существует"));

        user.getFavoritePosts().add(post);
        userRepository.save(user);
        return ResponseEntity
                .ok()
                .header("Content-Type", "text/html; charset=utf-8")
                .body("Мероприятие добавлено в избранное");
    }
    @Transactional
    public ResponseEntity<String> removePostFromFavorites(Long postId, String username) throws PostNotFoundException {
        Post post = postService.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Мероприятие с таким id не найдено"));
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Пользователя с таким именем не существует"));

        user.getFavoritePosts().remove(post);
        userRepository.save(user);
        return ResponseEntity
                .ok()
                .header("Content-Type", "text/html; charset=utf-8")
                .body("Мероприятие удалено из избранного");
    }
    @Transactional
    public Set<PostResponseDto> getFavoritePosts(String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Пользователя с таким именем не существует"));

        return user.getFavoritePosts().stream()
                .map(postMapper::postDtoFromPost)
                .collect(Collectors.toSet());
    }
}
