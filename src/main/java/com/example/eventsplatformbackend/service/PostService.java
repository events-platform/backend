package com.example.eventsplatformbackend.service;

import com.example.eventsplatformbackend.domain.dto.request.PostCreationDto;
import com.example.eventsplatformbackend.adapter.repository.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public ResponseEntity<String> savePost(PostCreationDto postCreationDto){
        // TODO дописать сохранение в базу после готовности фичи с jwt токенами
        return ResponseEntity.ok().body("this is stub method, this post was not saved.");
    }
}
