// R2StorageService.java
package com.aditi.dripyard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class R2StorageService {
    private final S3Client r2Client;

    public String uploadImage(Path filePath, String fileName) {
        String bucket = "<your-bucket-name>";
        r2Client.putObject(
                PutObjectRequest.builder().bucket(bucket).key(fileName).build(),
                filePath
        );
        // Public URL format (if bucket is public)
        return "https://" + bucket + ".<accountid>.r2.cloudflarestorage.com/" + fileName;
    }
}
