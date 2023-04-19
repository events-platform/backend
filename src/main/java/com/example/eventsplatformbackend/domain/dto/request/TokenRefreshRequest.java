package com.example.eventsplatformbackend.domain.dto.request;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String accessToken;
    private String refreshToken;
}
