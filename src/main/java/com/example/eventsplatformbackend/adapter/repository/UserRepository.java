package com.example.eventsplatformbackend.adapter.repository;

import com.example.eventsplatformbackend.domain.entity.Post;
import com.example.eventsplatformbackend.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByEmail(String email);
    boolean existsUserByUsername(String username);
    boolean existsUserByPhone(String phone);
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    User getUserByUsername(String username);
    Integer countUsersBySubscribedPosts(Post post);
    List<User> getUsersBySubscribedPosts(Post post);
    @Query("SELECT p FROM Post p WHERE p.owner.id = ?1")
    Page<Post> findAllUserCreatedPosts(Long userId, Pageable pageable);
    @Query("SELECT p FROM Post p INNER JOIN User u WHERE u.id = ?1 AND p MEMBER OF u.subscribedPosts")
    Page<Post> findAllUserSubscribedPosts(Long userId, Pageable pageable);
    @Query("SELECT p FROM Post p INNER JOIN User u WHERE u.id = ?1 AND p MEMBER OF u.favoritePosts")
    Page<Post> findAllUserFavoritePosts(Long userId, Pageable pageable);
}
