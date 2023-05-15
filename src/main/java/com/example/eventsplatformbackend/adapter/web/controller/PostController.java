package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.domain.dto.request.PostCreationDto;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDto;
import com.example.eventsplatformbackend.service.post.PostService;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("post")
@Slf4j
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SneakyThrows
    public ResponseEntity<String> createPost(
            @Valid @ModelAttribute PostCreationDto postCreationDto,
            Principal principal){
        return postService.savePost(postCreationDto, principal);
    }
    @GetMapping("/{postId}")
    @SneakyThrows
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId){
        return postService.getPostById(postId);
    }
    @GetMapping("/all")
    public ResponseEntity<List<PostResponseDto>> getAllPosts(){
        return postService.getAllPosts();
    }
}
