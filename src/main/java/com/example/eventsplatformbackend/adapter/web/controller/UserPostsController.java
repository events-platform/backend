package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.domain.dto.request.PostIdDto;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDto;
import com.example.eventsplatformbackend.service.user.UserPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "user/post")
@RequiredArgsConstructor
@Slf4j
public class UserPostsController {
    private final UserPostService userPostService;

    @GetMapping("/created")
    @SneakyThrows
    public ResponseEntity<List<PostResponseDto>> getUserCreatedPosts(@RequestParam String username){
        return userPostService.getUserCreatedPosts(username);
    }
    @PostMapping("/favorite")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SneakyThrows
    public ResponseEntity<String> likePost(@Valid @RequestBody PostIdDto postIdDto, Principal principal){
        return userPostService.addPostToFavorites(postIdDto, principal.getName());
    }
    @GetMapping(value = "/favorite")
    @SneakyThrows
    public List<PostResponseDto> getFavoritePosts(@RequestParam String username){
        return userPostService.getFavoritePosts(username);
    }
    @DeleteMapping(value = "/favorite")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SneakyThrows
    public ResponseEntity<String> deleteFromFavoritePosts(@Valid @RequestBody PostIdDto postIdDto, Principal principal){
        return userPostService.removePostFromFavorites(postIdDto, principal.getName());
    }
    @PostMapping("/subscriptions")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SneakyThrows
    public ResponseEntity<String> subscribeToPost(@Valid @RequestBody PostIdDto postIdDto, Principal principal){
        return userPostService.subscribeToPost(postIdDto, principal.getName());
    }
    @GetMapping("/subscriptions")
    @SneakyThrows
    public List<PostResponseDto> getUserSubscriptions(@RequestParam String username){
        return userPostService.getUserSubscriptions(username);
    }
    @DeleteMapping("/subscriptions")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SneakyThrows
    public ResponseEntity<String> unsubscribeFromPost(@Valid @RequestBody PostIdDto postIdDto, Principal principal){
        return userPostService.unsubscribeFromPost(postIdDto, principal.getName());
    }
}
