package com.example.eventsplatformbackend.service.s3;

import com.amazonaws.services.s3.model.S3Object;
import com.example.eventsplatformbackend.common.exception.EmptyFileException;
import com.example.eventsplatformbackend.common.exception.UnsupportedExtensionException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    String uploadImageAndGetUrl(String path, MultipartFile file) throws IOException, EmptyFileException, UnsupportedExtensionException;
    S3Object download(String path);
    void delete(String path);
}
