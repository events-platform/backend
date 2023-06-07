package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.domain.dto.request.PostIdDto;
import com.example.eventsplatformbackend.domain.dto.response.PersonalizedPostResponseDtoImpl;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDtoImpl;
import com.example.eventsplatformbackend.service.user.UserPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    @GetMapping(value = "/{postId}", produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public PersonalizedPostResponseDtoImpl getPersonalizedPostByToken(@PathVariable Long postId, Principal principal){
        return userPostService.getPersonalizedPost(postId, principal);
    }
    @GetMapping(value = "/created", produces = "application/json; charset=utf-8")
    public List<PostResponseDtoImpl> getUserCreatedPosts(@RequestParam String username){
        return userPostService.getUserCreatedPosts(username);
    }
    @PostMapping(value = "/favorite", produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String likePost(@Valid @RequestBody PostIdDto postIdDto, Principal principal){
        return userPostService.addPostToFavorites(postIdDto, principal.getName());
    }
    @GetMapping(value = "/favorite", produces = "application/json; charset=utf-8")
    public List<PostResponseDtoImpl> getFavoritePosts(@RequestParam String username){
        return userPostService.getFavoritePosts(username);
    }
    @DeleteMapping(value = "/favorite", produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String deleteFromFavoritePosts(@Valid @RequestBody PostIdDto postIdDto, Principal principal){
        return userPostService.removePostFromFavorites(postIdDto, principal.getName());
    }
    @PostMapping(value = "/subscriptions", produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> subscribeToPost(@Valid @RequestBody PostIdDto postIdDto, Principal principal){
        return userPostService.subscribeToPost(postIdDto, principal.getName());
    }
    @GetMapping(value = "/subscriptions", produces = "application/json; charset=utf-8")
    public List<PostResponseDtoImpl> getUserSubscriptions(@RequestParam String username){
        return userPostService.getUserSubscriptions(username);
    }
    @DeleteMapping(value = "/subscriptions", produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String unsubscribeFromPost(@Valid @RequestBody PostIdDto postIdDto, Principal principal){
        return userPostService.unsubscribeFromPost(postIdDto, principal.getName());
    }
}
