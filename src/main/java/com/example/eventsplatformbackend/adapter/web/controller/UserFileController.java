package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.service.user.UserFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping(path = "user/files")
@RequiredArgsConstructor
@Slf4j
public class UserFileController {

    private final UserFileService userFileService;

    @PostMapping(path = "/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String uploadImage(@RequestParam("file") MultipartFile uploadedFile, Principal principal) {
        return userFileService.setUserAvatarAndGetLink(uploadedFile, principal);
    }
}
