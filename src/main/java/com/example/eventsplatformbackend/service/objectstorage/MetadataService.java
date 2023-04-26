package com.example.eventsplatformbackend.service.objectstorage;

import com.amazonaws.services.s3.model.S3Object;
import com.example.eventsplatformbackend.exception.EmptyFileException;
import com.example.eventsplatformbackend.exception.UnsupportedExtensionException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MetadataService {
    String uploadAndGetUrl(String path, MultipartFile file) throws IOException, EmptyFileException, UnsupportedExtensionException;
    S3Object download(String path);
    void delete(String path);
}
