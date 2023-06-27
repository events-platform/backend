package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.domain.dto.request.PostIdDto;
import com.example.eventsplatformbackend.domain.dto.response.PersonalizedPostResponseDtoImpl;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDtoImpl;
import com.example.eventsplatformbackend.domain.enumeration.EUserPostType;
import com.example.eventsplatformbackend.service.user.UserPostService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    @PageableAsQueryParam
    @GetMapping(value = "/created", produces = "application/json; charset=utf-8")
    public Page<PostResponseDtoImpl> getUserCreatedPosts(
            @RequestParam String username,
            @RequestParam(defaultValue = "false") Boolean showEnded,
            @Parameter(hidden = true)
            @PageableDefault(sort = {"beginDate", "name"}, direction = Sort.Direction.ASC) Pageable pageable){
        return userPostService.getUserProfilePosts(EUserPostType.CREATED, username, showEnded, pageable);
    }
    @PostMapping(value = "/favorite", produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String addPostToFavorites(@Valid @RequestBody PostIdDto postIdDto, Principal principal){
        return userPostService.addPostToFavorites(postIdDto, principal.getName());
    }
    @PageableAsQueryParam
    @GetMapping(value = "/favorite", produces = "application/json; charset=utf-8")
    public Page<PostResponseDtoImpl> getFavoritePosts(
            @RequestParam String username,
            @RequestParam(defaultValue = "false") Boolean showEnded,
            @Parameter(hidden = true)
            @PageableDefault(sort = {"beginDate", "name"}, direction = Sort.Direction.ASC) Pageable pageable){
        return userPostService.getUserProfilePosts(EUserPostType.FAVORITE, username, showEnded, pageable);
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
    @PageableAsQueryParam
    @GetMapping(value = "/subscriptions", produces = "application/json; charset=utf-8")
    public Page<PostResponseDtoImpl> getUserSubscriptions(
            @RequestParam String username,
            @RequestParam(defaultValue = "false") Boolean showEnded,
            @Parameter(hidden = true)
            @PageableDefault(sort = {"beginDate", "name"}, direction = Sort.Direction.ASC) Pageable pageable){
        return userPostService.getUserProfilePosts(EUserPostType.SUBSCRIBED, username, showEnded, pageable);
    }
    @DeleteMapping(value = "/subscriptions", produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String unsubscribeFromPost(@Valid @RequestBody PostIdDto postIdDto, Principal principal){
        return userPostService.unsubscribeFromPost(postIdDto, principal.getName());
    }
}
