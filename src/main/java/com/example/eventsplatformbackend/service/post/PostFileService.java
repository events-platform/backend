package com.example.eventsplatformbackend.service.post;

import com.example.eventsplatformbackend.service.s3.S3ServiceImpl;
import com.example.eventsplatformbackend.config.AwsCredentials;
import com.google.common.io.Files;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Посредник между S3Service и PostService
 */
@Service
public class PostFileService {
    private final AwsCredentials awsCredentials;
    private final S3ServiceImpl s3Service;

    public PostFileService(AwsCredentials awsCredentials, S3ServiceImpl s3Service) {
        this.awsCredentials = awsCredentials;
        this.s3Service = s3Service;
    }

    /**
     *
     * Находит директорию для сохранения файла, дает ему имя и сохраняет с помощью S3Service
     * @param uploadedFile Файл, который должен быть сохранен
     * @return Публичная ссылка на файл
     */
    @SneakyThrows
    public String saveAndGetLink(MultipartFile uploadedFile) {
        String filename = String.format("%s.%s",
                UUID.randomUUID(),
                Files.getFileExtension(uploadedFile.getOriginalFilename()));
        String path = String.format("%s/%s", awsCredentials.getPostsDirectory(), filename);

        return s3Service.uploadImageAndGetUrl(path, uploadedFile);
    }
}
