package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.domain.dto.request.PostCreationDto;
import com.example.eventsplatformbackend.domain.dto.request.PostIdDto;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDto;
import com.example.eventsplatformbackend.domain.dto.response.UserDto;
import com.example.eventsplatformbackend.service.post.PostService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("post")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - issue with filtering or pagination parameters")
    })
    @PageableAsQueryParam
    @GetMapping(value = "/search", produces = "application/json; charset=utf-8")
    public Page<PostResponseDto> findPostsWithPagination(
            @RequestParam(required = false) LocalDateTime beginDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) List<String> organizer,
            @RequestParam(required = false) List<String> type,
            @RequestParam(required = false) List<String> format,
            @RequestParam(required = false, defaultValue = "false") Boolean showEnded,
            @RequestParam(required = false) String searchQuery,
            @Parameter(hidden = true)
            @PageableDefault(sort = {"beginDate", "name"}, direction = Sort.Direction.ASC) Pageable pageable){
        return postService.getPostsPaginationWithFilters(
                beginDate, endDate, organizer, type, format, showEnded, searchQuery, pageable);
    }
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String createPost(
            @RequestPart(value = "data") @Valid PostCreationDto postCreationDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal){
        return postService.savePost(postCreationDto, file, principal);
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Not found - post with this id not exists"),
            @ApiResponse(responseCode = "403", description = "Forbidden - access denied")
    })
    @DeleteMapping(produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String deletePost(@RequestBody PostIdDto postIdDto, Principal principal){
        return postService.deletePost(postIdDto.getPostId(), principal.getName());
    }
    @GetMapping(value = "/{postId}", produces = "application/json; charset=utf-8")
    public PostResponseDto getPostById(@PathVariable Long postId){
        return postService.getPostById(postId);
    }
    @GetMapping(value = "/subscribers/{postId}", produces = "application/json; charset=utf-8")
    public List<UserDto> getPostSubscribers(@PathVariable Long postId){
        return postService.getPostSubscribers(postId);
    }
    @GetMapping(value = "/all", produces = "application/json; charset=utf-8")
    public List<PostResponseDto> getAllPosts(){
        return postService.getAllPosts();
    }
}
