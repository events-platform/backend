package com.example.eventsplatformbackend.controller;

import com.example.eventsplatformbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping(path = "file")
@Slf4j
public class FilesController {
    private final UserService userService;

    public FilesController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void uploadImage(@RequestParam("file") MultipartFile uploadedFile, Principal principal){
        userService.uploadFile(uploadedFile, principal);
    }
}
