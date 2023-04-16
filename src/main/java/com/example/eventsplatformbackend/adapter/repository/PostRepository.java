package com.example.eventsplatformbackend.adapter.repository;

import com.example.eventsplatformbackend.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> getPostByName(String name);
}
