package com.example.eventsplatformbackend.repository;

import com.example.eventsplatformbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    void deleteUserByUsername(String username);
    Optional<User> findByUsername(String username);
}
