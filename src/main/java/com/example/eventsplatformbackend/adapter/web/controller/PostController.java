package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.domain.dto.request.PostCreationDto;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDto;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("post")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    @PageableAsQueryParam
    @GetMapping("/search")
    @SneakyThrows
    public ResponseEntity<PageImpl<PostResponseDto>> getPostsPagination(Pageable pageable){
        return postService.getPostsPagination(pageable);
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SneakyThrows
    public ResponseEntity<String> createPost(
            @RequestPart(value = "data") @Valid PostCreationDto postCreationDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal){
        return postService.savePost(postCreationDto, file, principal);
    }
    @GetMapping("/{postId}")
    @SneakyThrows
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId){
        return postService.getPostById(postId);
    }
    @GetMapping("/subscribers/{postId}")
    @SneakyThrows
    public ResponseEntity<List<UserDto>> getPostSubscribers(@PathVariable Long postId){
        return postService.getPostSubscribers(postId);
    }
    @GetMapping("/all")
    public ResponseEntity<List<PostResponseDto>> getAllPosts(){
        return postService.getAllPosts();
    }
}
