package com.example.eventsplatformbackend.adapter.web.controller;

import com.example.eventsplatformbackend.service.user.UserFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

/**
 * Работает с файлами пользователей
 */
@RestController
@RequestMapping(path = "user/files")
@Slf4j
public class UserFileController {
    private final UserFileService userFileService;

    public UserFileController(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    /**
     * Загружает файл и устанавливает его аватаркой пользователя, возвращает ссылку на загруженны файл.
     * @param uploadedFile Файл, который должен быть загружен
     * @param principal Авторизовавшийся пользователь
     * @return Ссылка на файл
     */
    @PostMapping(path = "/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile uploadedFile, Principal principal){
        return userFileService.setUserAvatarAndGetLink(uploadedFile, principal);
    }
}
