package com.example.eventsplatformbackend.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtTokenPair {
    @NotBlank
    private String accessToken;
    @NotBlank
    private String refreshToken;
}
