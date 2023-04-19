package com.example.eventsplatformbackend.adapter.repository;

import com.example.eventsplatformbackend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByEmail(String email);
    boolean existsUserByUsername(String username);
    boolean existsUserByPhone(String phone);
    void deleteUserByUsername(String username);
    Optional<User> findUserByUsername(String username);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
}
