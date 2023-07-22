package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.domain.dto.request.JwtRequest;
import com.example.eventsplatformbackend.domain.dto.request.RegistrationDto;
import com.example.eventsplatformbackend.domain.dto.request.JwtTokenPair;
import com.example.eventsplatformbackend.domain.dto.response.JwtResponse;
import com.example.eventsplatformbackend.service.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/signup", produces = "application/json; charset=utf-8")
    public JwtResponse signUp(@Valid @RequestBody RegistrationDto registrationDto) {
        return authService.signUp(registrationDto);
    }

    @PostMapping(value = "/login", produces = "application/json; charset=utf-8")
    public JwtResponse login(@Valid @RequestBody JwtRequest jwtRequest) {
        return authService.login(jwtRequest);
    }

    @PostMapping(value = "/logout", produces = "application/json; charset=utf-8")
    public String logout(@Valid @RequestBody JwtTokenPair jwtTokenPair) {
        return authService.logout(jwtTokenPair);
    }

    @PostMapping("/refresh")
    public JwtResponse refreshToken(@RequestBody JwtTokenPair jwtTokenPair) {
        return authService.refreshToken(jwtTokenPair);
    }
}
