package com.example.eventsplatformbackend.service.objectstorage;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.example.eventsplatformbackend.exception.EmptyFileException;
import com.example.eventsplatformbackend.exception.UnsupportedExtensionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Выполняет CRUD операции с S3 хранилищем.
 * Обертка для S3FileService
 */
@Component
@Slf4j
public class MetadataServiceImpl implements MetadataService{
    private final S3FileService s3FileService;

    public MetadataServiceImpl(S3FileService s3FileService) {
        this.s3FileService = s3FileService;
    }

    /**
     * Загружает файл в объектное хранилище и возвращает ссылку на его скачивание.
     */
    @Override
    public String uploadAndGetUrl(String path, MultipartFile file) throws IOException, EmptyFileException, UnsupportedExtensionException {
        if (file.isEmpty())
            throw new EmptyFileException("Cannot upload empty file");

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        s3FileService.uploadFile(
                path, objectMetadata, file.getInputStream());
        log.info("Uploaded file {}", path);
        return s3FileService.getLink(path);
    }

    @Override
    public S3Object download(String path) {
        return s3FileService.getFile(path);
    }

    @Override
    public void delete(String path) {
        s3FileService.deleteFile(path);
    }
}
