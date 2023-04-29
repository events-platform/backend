package com.example.eventsplatformbackend.adapter.objectstorage;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.example.eventsplatformbackend.exception.EmptyFileException;
import com.example.eventsplatformbackend.exception.UnsupportedExtensionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Выполняет CRUD операции с S3 хранилищем.
 * Обертка для S3Adapter
 */
@Component
@Slf4j
public class S3ServiceImpl implements S3Service {
    private final S3Adapter s3Adapter;

    public S3ServiceImpl(S3Adapter s3Adapter) {
        this.s3Adapter = s3Adapter;
    }

    /**
     * Провверяет
     * Загружает файл в объектное хранилище и возвращает ссылку на его скачивание.
     */
    @Override
    public String uploadImageAndGetUrl(String path, MultipartFile file) throws IOException, EmptyFileException, UnsupportedExtensionException {
        if (file.isEmpty())
            throw new EmptyFileException("Cannot upload empty file");

        if(!checkFileExtension(path, Arrays.asList("jpg", "png", "jpeg"))){
            throw new UnsupportedExtensionException(String.format("Wrong extension of %s", path));
        }

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

    private boolean checkFileExtension(String filename, List<String> acceptedExtensions){
        Optional<String> fileExtension = Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));

        return fileExtension.isPresent() && acceptedExtensions.contains(fileExtension.get());
    }
}
