package com.example.eventsplatformbackend.service.post;

import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.domain.dto.request.PostCreationDto;
import com.example.eventsplatformbackend.adapter.repository.PostRepository;
import com.example.eventsplatformbackend.domain.entity.Post;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.exception.InvalidDateException;
import com.example.eventsplatformbackend.exception.PostAlreadyExistsException;
import com.example.eventsplatformbackend.mapper.PostMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final PostFileService postFileService;

    public PostService(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper, PostFileService postFileService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
        this.postFileService = postFileService;
    }

    @Transactional(propagation= Propagation.REQUIRED)
    public ResponseEntity<String> savePost(PostCreationDto postCreationDto, Principal principal) throws PostAlreadyExistsException, InvalidDateException {
        Post post = postMapper.postCreationDtoToPost(postCreationDto);

        if (postRepository.existsPostByBeginDateAndName(post.getBeginDate(), post.getName())){
            throw new PostAlreadyExistsException("Post with same date and begin date already exists");
        }
        if (post.getEndDate() != null && (post.getBeginDate().after(post.getEndDate()))){
            throw new InvalidDateException("End date cannot be before begin date");
        }
        if(postCreationDto.getFile() != null){
            String link = postFileService.saveAndGetLink(postCreationDto.getFile());
            post.setImage(link);
        }

        User user = userRepository.getUserByUsername(principal.getName());
        user.getCreatedPosts().add(post);
        post.setOwner(user);

        postRepository.save(post);
        userRepository.save(user);
        return ResponseEntity
                .ok()
                .header("Content-Type", "text/html; charset=utf-8")
                .body(String.format("Post %s saved", postCreationDto.getName()));
    }
}
