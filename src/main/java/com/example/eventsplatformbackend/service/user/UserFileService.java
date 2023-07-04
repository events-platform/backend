package com.example.eventsplatformbackend.service.user;

import com.amazonaws.services.s3.model.S3Object;
import com.example.eventsplatformbackend.adapter.objectstorage.S3Adapter;
import com.example.eventsplatformbackend.common.exception.UserNotFoundException;
import com.example.eventsplatformbackend.service.s3.S3ServiceImpl;
import com.example.eventsplatformbackend.adapter.repository.UserRepository;
import com.example.eventsplatformbackend.config.AwsCredentials;
import com.example.eventsplatformbackend.domain.entity.User;
import com.google.common.io.Files;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Работает с файлами пользователя
 */
@Service
@Slf4j
public class UserFileService {
    private final UserRepository userRepository;
    private final S3Adapter s3Adapter;
    private final S3ServiceImpl s3Service;
    private final AwsCredentials awsCredentials;

    public UserFileService(UserRepository userRepository, S3Adapter s3Adapter, S3ServiceImpl s3Service, AwsCredentials awsCredentials) {
        this.userRepository = userRepository;
        this.s3Adapter = s3Adapter;
        this.s3Service = s3Service;
        this.awsCredentials = awsCredentials;
    }
    @Transactional
    public String setUserAvatarAndGetLink(MultipartFile uploadedFile, Principal principal){
        String filename = String.format("%s.%s",
                UUID.randomUUID(),
                Files.getFileExtension(uploadedFile.getOriginalFilename()));
        String path = String.format("%s/%s/%s", awsCredentials.getUsersDirectory(), principal.getName(), filename);

        User user = userRepository.getUserByUsername(principal.getName());
        String oldAvatar = user.getAvatar();
        String url;

        if(oldAvatar != null && !oldAvatar.equals(path) && !oldAvatar.equals(s3Adapter.getDefaultAvatarDirectory())) {
            url = s3Service.uploadImageAndGetUrl(path, uploadedFile);
            user.setAvatar(url);
        }
        userRepository.save(user);

        return user.getAvatar();
    }

    public List<S3Object> getUserFiles(String username) {

        log.info("downloading {} files...", username);

        if (userRepository.getUserByUsername(username) == null){
            throw new UserNotFoundException(String.format("Пользователя с именем %s не существует", username));
        }

        var filePrefix = String.format("%s/%s/", awsCredentials.getUsersDirectory(), username);
        var filenames = s3Adapter.getObjectSummaries(filePrefix);
        var result = new ArrayList<S3Object>();
        for (var filename : filenames){
            var file = s3Adapter.getFile(filename);
            result.add(file);
        }

        return result;
    }
}
