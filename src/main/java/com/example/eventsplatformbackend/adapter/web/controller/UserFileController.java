package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.service.user.UserFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping(path = "user/files")
@Slf4j
public class UserFileController {
    private final UserFileService userFileService;

    public UserFileController(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    @PostMapping("/avatar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile uploadedFile, Principal principal){
        return userFileService.setUserAvatar(uploadedFile, principal);
    }
}
