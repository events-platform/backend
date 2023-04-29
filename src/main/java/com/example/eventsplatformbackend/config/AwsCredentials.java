package com.example.eventsplatformbackend.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
public class AwsCredentials {
    @Value("${aws.access-key}")
    String accessKey;
    @Value("${aws.secret-key}")
    String secretKey;
    @Value("${aws.region}")
    String region;
    @Value("${aws.url}")
    String url;
    @Value("${aws.bucket-name}")
    String bucketName;
    @Value("${aws.users-root-directory}")
    String usersDirectory;
    @Value("${aws.posts-root-directory}")
    String postsDirectory;
}
