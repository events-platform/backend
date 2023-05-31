package com.example.eventsplatformbackend.adapter.objectstorage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.example.eventsplatformbackend.config.AwsCredentials;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
/**
 * Выполняет операции над файлами в объектном хранилище.
 * Умеет сохранять, скачивать и удалять файлы из хранилища.
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
            InputStream inputStream) {
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
}
