package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping(path = "user/avatar")
@Slf4j
public class AvatarController {
    private final UserService userService;

    public AvatarController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile uploadedFile, Principal principal){
        return userService.setUserAvatar(uploadedFile, principal);
    }
    @SneakyThrows
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> getAvatar(Principal principal){
        return userService.getUserAvatar(principal);
    }
}
