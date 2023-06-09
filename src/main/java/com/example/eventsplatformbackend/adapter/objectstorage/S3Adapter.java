package com.example.eventsplatformbackend.adapter.objectstorage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.eventsplatformbackend.config.AwsCredentials;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Возвращает список всех объектов в директории
     * @param prefix Префикс элементов, которые необходимо найти
     * @return Список объектов по указанному префиксу
     */
    @Cacheable(value = "objectSummaries")
    public List<String> getObjectSummaries(String prefix){
        List<String> summaries = new ArrayList<>();

        ListObjectsV2Request req = new ListObjectsV2Request()
                .withBucketName(awsCredentials.getBucketName())
                .withPrefix(prefix)
                .withDelimiter("/");
        ListObjectsV2Result listing = amazonS3.listObjectsV2(req);
        for (S3ObjectSummary summary: listing.getObjectSummaries()) {
            summaries.add(summary.getKey());
        }
        return summaries;
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
