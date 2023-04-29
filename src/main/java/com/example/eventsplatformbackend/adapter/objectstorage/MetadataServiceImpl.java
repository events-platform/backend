package com.example.eventsplatformbackend.adapter.objectstorage;

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
 * Обертка для S3Adapter
 */
@Component
@Slf4j
public class MetadataServiceImpl implements MetadataService{
    private final S3Adapter s3Adapter;

    public MetadataServiceImpl(S3Adapter s3Adapter) {
        this.s3Adapter = s3Adapter;
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

        s3Adapter.uploadFile(
                path, objectMetadata, file.getInputStream());
        log.info("Uploaded file {}", path);
        return s3Adapter.getLink(path);
    }

    @Override
    public S3Object download(String path) {
        return s3Adapter.getFile(path);
    }

    @Override
    public void delete(String path) {
        s3Adapter.deleteFile(path);
    }
}
