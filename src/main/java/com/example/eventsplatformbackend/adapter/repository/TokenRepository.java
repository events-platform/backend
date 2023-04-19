package com.example.eventsplatformbackend.adapter.repository;

import com.example.eventsplatformbackend.domain.entity.JwtToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<JwtToken, String> {
    boolean existsJwtTokenByBody(String body);
}
