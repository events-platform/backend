package com.example.eventsplatformbackend.service;

import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.config.AwsCredentials;
import com.example.eventsplatformbackend.domain.entity.User;
import com.example.eventsplatformbackend.service.objectstorage.MetadataServiceImpl;
import com.example.eventsplatformbackend.service.objectstorage.S3FileService;
import com.google.common.io.Files;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.UUID;

/**
 * Занимается работой с пользовательскими файлами, которые должны быть созранены в объектное хранилище.
 * Взаимодействует с базой данных и S3FileService.
 */
@Service
@Slf4j
public class UserFileService {
    private final UserRepository userRepository;
    private final S3FileService fileService;
    private final MetadataServiceImpl metadataService;
    private final AwsCredentials awsCredentials;

    public UserFileService(UserRepository userRepository, S3FileService fileService, MetadataServiceImpl metadataService, AwsCredentials awsCredentials) {
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.metadataService = metadataService;
        this.awsCredentials = awsCredentials;
    }

    @SneakyThrows
    @Transactional
    public ResponseEntity<String> setUserAvatarAndGetLink(MultipartFile uploadedFile, Principal principal){
        String filename = String.format("%s.%s",
                UUID.randomUUID(),
                Files.getFileExtension(uploadedFile.getOriginalFilename()));
        String path = String.format("%s/%s/%s", awsCredentials.getRootDirectory(), principal.getName(), filename);

        User user = userRepository.getUserByUsername(principal.getName());
        String oldAvatar = user.getAvatar();
        String url;
        if(oldAvatar != null && !oldAvatar.equals(path) && !oldAvatar.equals(fileService.getDefaultAvatarDirectory())) {
            url = metadataService.uploadAndGetUrl(path, uploadedFile);
            user.setAvatar(url);
        }
        userRepository.save(user);

        return ResponseEntity.status(201).body(user.getAvatar());
    }
}
