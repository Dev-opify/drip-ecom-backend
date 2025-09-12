// R2Config.java
package com.aditi.dripyard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class R2Config {
    @Bean
    public S3Client r2Client() {
        return S3Client.builder()
                .endpointOverride(URI.create("https://<accountid>.r2.cloudflarestorage.com"))
                .region(Region.of("auto"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("<ACCESS_KEY_ID>", "<SECRET_ACCESS_KEY>")
                        )
                )
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
