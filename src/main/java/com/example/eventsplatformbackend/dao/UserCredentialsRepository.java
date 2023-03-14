package com.example.eventsplatformbackend.dao;

import com.example.eventsplatformbackend.model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {
}
