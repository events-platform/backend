package com.example.eventsplatformbackend.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtTokenPair {
    @NotBlank(message = "Access Token is required")
    private String accessToken;
    @NotBlank(message = "Refresh Token is required")
    private String refreshToken;
}
