package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.domain.dto.request.JwtRequest;
import com.example.eventsplatformbackend.domain.dto.request.RegistrationDto;
import com.example.eventsplatformbackend.domain.dto.request.JwtTokenPair;
import com.example.eventsplatformbackend.domain.dto.response.JwtResponse;
import com.example.eventsplatformbackend.service.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping(path = "auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;


    @SneakyThrows
    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> signUp(@Valid @RequestBody RegistrationDto registrationDto){
        return authService.signUp(registrationDto);
    }
    @SneakyThrows
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody JwtRequest jwtRequest){
        return authService.login(jwtRequest);
    }
    @SneakyThrows
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody JwtTokenPair jwtTokenPair){
        return authService.logout(jwtTokenPair);
    }
    @SneakyThrows
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody JwtTokenPair jwtTokenPair){
        return authService.refreshToken(jwtTokenPair);
    }
}
