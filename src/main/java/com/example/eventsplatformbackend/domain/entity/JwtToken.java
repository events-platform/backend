package com.example.eventsplatformbackend.domain.entity;


import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("JwtToken")
@Getter
@NoArgsConstructor
public class JwtToken {
    @GeneratedValue
    private String id;
    private String body;

    public JwtToken(String body) {
        this.body = body;
    }
}
