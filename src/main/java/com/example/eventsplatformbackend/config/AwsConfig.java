package com.example.eventsplatformbackend.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {
    private final AwsCredentials awsCredentials;

    public AwsConfig(AwsCredentials awsCredentials) {
        this.awsCredentials = awsCredentials;
    }

    public AWSCredentials credentials() {
        return new BasicAWSCredentials(
                awsCredentials.getAccessKey(),
                awsCredentials.getSecretKey()
        );
    }

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials()))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                awsCredentials.getUrl(), awsCredentials.getRegion()
                        )
                )
                .build();
    }

}
