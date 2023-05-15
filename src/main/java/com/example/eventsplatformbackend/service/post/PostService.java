package com.example.eventsplatformbackend.service.post;

import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.domain.dto.request.PostCreationDto;
import com.example.eventsplatformbackend.adapter.repository.PostRepository;
import com.example.eventsplatformbackend.domain.dto.response.PostResponseDto;
import com.example.eventsplatformbackend.domain.entity.Post;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.exception.InvalidDateException;
import com.example.eventsplatformbackend.exception.PostAlreadyExistsException;
import com.example.eventsplatformbackend.exception.PostNotFoundException;
import com.example.eventsplatformbackend.mapper.PostMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
            throw new PostAlreadyExistsException("Мероприятие с таким названием и датой начала уже существует");
        }
        if (post.getBeginDate().isAfter(post.getEndDate())
                || post.getBeginDate().isEqual(post.getEndDate())){
            throw new InvalidDateException("Мероприятие не может кончаться раньше, чем начнется");
        }
        if(post.getBeginDate().isBefore(LocalDate.now().atStartOfDay())){
            throw new InvalidDateException("Мероприятие не может начинаться раньше сегодняшнего дня");
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
                .body("Мероприятие успешно сохранено");
    }
    @Transactional
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        List<PostResponseDto> postResponseDtos = postRepository.findAll().stream()
                .map(postMapper::postDtoFromPost)
                .toList();
        return ResponseEntity.ok(postResponseDtos);
    }

    public Optional<Post> findById(Long postId) {
        return postRepository.findById(postId);
    }

    public ResponseEntity<PostResponseDto> getPostById(Long postId) throws PostNotFoundException {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Мероприятия с таким id не существует"));
        return ResponseEntity.ok(postMapper.postDtoFromPost(post));
    }
}
