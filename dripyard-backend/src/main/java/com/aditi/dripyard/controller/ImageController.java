package com.aditi.dripyard.controller;

import com.aditi.dripyard.service.R2StorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final R2StorageService storageService;

    public ImageController(R2StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String fileKey = UUID.randomUUID() + "_" + file.getOriginalFilename();

        storageService.uploadFile(fileKey, file.getInputStream(), file.getSize(), file.getContentType());
        return ResponseEntity.ok("Uploaded with key: " + fileKey);
    }

    @GetMapping("/{key}")
    public ResponseEntity<byte[]> getImage(@PathVariable String key) {
        byte[] imageData = storageService.downloadFile(key);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)  // or detect type dynamically
                .body(imageData);
    }
}
