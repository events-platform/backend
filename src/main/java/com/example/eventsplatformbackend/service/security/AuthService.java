package com.example.eventsplatformbackend.service.security;

import com.example.eventsplatformbackend.adapter.repository.TokenRepository;
import com.example.eventsplatformbackend.domain.dto.request.JwtRequest;
import com.example.eventsplatformbackend.domain.dto.request.RegistrationDto;
import com.example.eventsplatformbackend.domain.dto.request.JwtTokenPair;
import com.example.eventsplatformbackend.domain.dto.response.JwtResponse;
import com.example.eventsplatformbackend.domain.entity.JwtToken;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.common.exception.MalformedTokenException;
import com.example.eventsplatformbackend.security.JwtUtil;
import com.example.eventsplatformbackend.service.user.UserService;
import com.example.eventsplatformbackend.service.factory.JwtFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Занимается регистрацией, авторизацией и заменой jwt токенов
 */
@Slf4j
@Service
public class AuthService {
    @Value("${jwt.access-token-expiration-time}")
    private Long accessTokenExpirationTime;
    @Value("${jwt.refresh-token-expiration-time}")
    private Long refreshTokenExpirationTime;
    private final JwtUtil jwtUtil;
    private final JwtFactory jwtFactory;
    private final UserService userService;
    private final TokenRepository tokenRepository;

    public AuthService(JwtUtil jwtUtil, JwtFactory jwtFactory, UserService userService, TokenRepository tokenRepository) {
        this.jwtUtil = jwtUtil;
        this.jwtFactory = jwtFactory;
        this.userService = userService;
        this.tokenRepository = tokenRepository;
    }

    public JwtResponse signUp(RegistrationDto registrationDto) {
        User user = userService.createUser(registrationDto);
        return jwtFactory.getJwtResponse(user);
    }

    public JwtResponse login(JwtRequest jwtRequest) {
        User user = userService.login(jwtRequest);
        return jwtFactory.getJwtResponse(user);
    }

    public String logout(JwtTokenPair jwtTokenPair){
        // Save used tokens to redis blacklist
        tokenRepository.saveAll(List.of(
                new JwtToken(jwtTokenPair.getAccessToken(), accessTokenExpirationTime),
                new JwtToken(jwtTokenPair.getRefreshToken(), refreshTokenExpirationTime))
        );
        return "Вы вышли из системы";
    }

    public JwtResponse refreshToken(JwtTokenPair jwtTokenPair) throws MalformedTokenException {
        User user = userService.findById(jwtUtil.extractUserId(jwtTokenPair.getAccessToken())).orElseThrow(() ->
            new MalformedTokenException("Malformed JWT, cannot extract user data"));

        // check if refresh token is valid and not blacklisted
        if (jwtUtil.validateRefreshToken(jwtTokenPair.getRefreshToken())
                && !tokenRepository.existsJwtTokenByBody(jwtTokenPair.getRefreshToken())){
            // save used tokens to blacklist
            tokenRepository.saveAll(List.of(
                    new JwtToken(jwtTokenPair.getAccessToken(), accessTokenExpirationTime),
                    new JwtToken(jwtTokenPair.getRefreshToken(), refreshTokenExpirationTime)));
            // generate new tokens
            return jwtFactory.getJwtResponse(user);
        }
        throw new MalformedTokenException("Malformed JWT, cannot extract user data");
    }
}
