package com.example.eventsplatformbackend.service.security;

import com.example.eventsplatformbackend.domain.dto.request.JwtRequest;
import com.example.eventsplatformbackend.domain.dto.request.RegistrationDto;
import com.example.eventsplatformbackend.domain.dto.request.TokenRefreshRequest;
import com.example.eventsplatformbackend.domain.dto.response.JwtResponse;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.exception.MalformedTokenException;
import com.example.eventsplatformbackend.exception.UserAlreadyExistsException;
import com.example.eventsplatformbackend.exception.WrongPasswordException;
import com.example.eventsplatformbackend.security.JwtUtil;
import com.example.eventsplatformbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthService(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    public ResponseEntity<JwtResponse> signUp(RegistrationDto registrationDto) throws UserAlreadyExistsException {
        User user = userService.createUser(registrationDto);
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        return ResponseEntity.status(201).body(new JwtResponse(accessToken, refreshToken));
    }

    public ResponseEntity<JwtResponse> login(JwtRequest jwtRequest) throws WrongPasswordException {
        User user = userService.login(jwtRequest);
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
    }

    public ResponseEntity<JwtResponse> refreshToken(TokenRefreshRequest tokenRefreshRequest) throws MalformedTokenException {
        Optional<User> user = userService.findById(jwtUtil.extractId(tokenRefreshRequest.getAccessToken()));

        if (user.isPresent() && jwtUtil.validateToken(tokenRefreshRequest.getRefreshToken())){
            String accessToken = jwtUtil.generateAccessToken(user.get());
            String refreshToken = jwtUtil.generateRefreshToken(user.get());
            return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
        }
        throw new MalformedTokenException("Malformed jwt, cannot extract user data");
    }
}
