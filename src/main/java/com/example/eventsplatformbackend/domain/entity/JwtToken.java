package com.example.eventsplatformbackend.domain.entity;


import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@RedisHash("JwtToken")
@Getter
public class JwtToken implements Serializable {
    private String id;
    @Indexed
    private String body;
    @TimeToLive
    private Long expirationInSeconds;

    public JwtToken(String body, Long expirationInSeconds) {
        this.body = body;
        this.expirationInSeconds = expirationInSeconds;
    }
}
