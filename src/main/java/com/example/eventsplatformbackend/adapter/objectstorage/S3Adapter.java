package com.example.eventsplatformbackend.adapter.objectstorage;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.example.eventsplatformbackend.config.AwsCredentials;
import com.example.eventsplatformbackend.exception.UnsupportedExtensionException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Выполняет операции над файлами в объектном хранилище.
 * Умеет сохранять (асинхронно), скачивать и удалять файлы из хранилища.
 */
@Component
@Slf4j
public class S3Adapter {
    @Getter
    @Value("${aws.default-avatar-dir}")
    private String defaultAvatarDirectory;
    private final AmazonS3 amazonS3;
    private final AwsCredentials awsCredentials;

    public S3Adapter(AmazonS3 amazonS3, AwsCredentials awsCredentials) {
        this.amazonS3 = amazonS3;
        this.awsCredentials = awsCredentials;
    }
    @Async
    public void uploadFile(String path,
            ObjectMetadata objectMetadata,
            InputStream inputStream)
            throws SdkClientException, UnsupportedExtensionException {
        if(!checkExtension(path, Arrays.asList("jpg", "png", "jpeg"))){
            throw new UnsupportedExtensionException(String.format("Wrong extension of %s", path));
        }

        log.info("Saving {} to S3", path);
        amazonS3.putObject(awsCredentials.getBucketName(), path, inputStream, objectMetadata);
    }

    public S3Object getFile(String filename) {
        return amazonS3.getObject(awsCredentials.getBucketName(), filename);
    }

    public void deleteFile(String path){
        amazonS3.deleteObject(awsCredentials.getBucketName(), path);
    }

    public String getLink(String path){
        return String.format("%s/%s/%s", awsCredentials.getUrl(), awsCredentials.getBucketName(), path);
    }

    private boolean checkExtension(String filename, List<String> acceptedExtensions){
        Optional<String> fileExtension = Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));

        return fileExtension.isPresent() && acceptedExtensions.contains(fileExtension.get());
    }
}
