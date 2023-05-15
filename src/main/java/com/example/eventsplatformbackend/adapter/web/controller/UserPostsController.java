package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.domain.dto.response.PostResponseDto;
import com.example.eventsplatformbackend.service.user.UserPostService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "user/post")
@Slf4j
public class UserPostsController {
    private final UserPostService userPostService;

    public UserPostsController(UserPostService userPostService) {
        this.userPostService = userPostService;
    }

    @GetMapping("/created")
    public ResponseEntity<List<PostResponseDto>> getUserCreatedPosts(@RequestParam String username){
        return userPostService.getUserCreatedPosts(username);
    }
    @PostMapping("/favorite")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SneakyThrows
    public ResponseEntity<String> likePost(@RequestParam Long postId, Principal principal){
        return userPostService.addPostToFavorites(postId, principal.getName());
    }
    @GetMapping(value = "/favorite")
    public Set<PostResponseDto> getFavoritePosts(@RequestParam String username){
        return userPostService.getFavoritePosts(username);
    }
    @DeleteMapping(value = "/favorite")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SneakyThrows
    public ResponseEntity<String> deleteFromFavoritePosts(Long postId, Principal principal){
        return userPostService.removePostFromFavorites(postId, principal.getName());
    }
}
