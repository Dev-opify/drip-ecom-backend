package com.aditi.dripyard.controller;

import com.aditi.dripyard.service.ProductService;
import com.aditi.dripyard.exception.ProductException;
import com.aditi.dripyard.service.R2StorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final R2StorageService storageService;
    private final ProductService productService;

    public ImageController(R2StorageService storageService, ProductService productService) {
        this.storageService = storageService;
        this.productService = productService;
    }

    /**
     * Generates a URL-safe key from the original filename by:
     * - Removing accents and diacritical marks
     * - Replacing spaces with hyphens
     * - Removing special characters
     * - Adding a unique UUID prefix
     */
    private String generateSafeKey(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return UUID.randomUUID().toString() + ".jpg";
        }
        
        // Normalize and remove accents
        String normalized = Normalizer.normalize(originalFilename, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutAccents = pattern.matcher(normalized).replaceAll("");
        
        // Replace spaces with hyphens and convert to lowercase
        String withHyphens = withoutAccents.trim().replaceAll("\\s+", "-").toLowerCase();
        
        // Remove all characters except alphanumeric, hyphens, dots, and underscores
        String sanitized = withHyphens.replaceAll("[^a-z0-9._-]", "");
        
        // Add UUID prefix to ensure uniqueness
        return UUID.randomUUID().toString() + "_" + sanitized;
    }

    @PostMapping("/upload")
    public ResponseEntity<java.util.Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        String fileKey = generateSafeKey(file.getOriginalFilename());
        
        storageService.uploadFile(fileKey, file.getInputStream(), file.getSize(), file.getContentType());

        return ResponseEntity.ok(java.util.Map.of("key", fileKey));
    }

    @GetMapping("/debug/list")
    public ResponseEntity<java.util.Map<String, Object>> listBucketContents() {
        try {
            // This is for debugging only - remove in production
            var listRequest = software.amazon.awssdk.services.s3.model.ListObjectsV2Request.builder()
                    .bucket(storageService.getBucketName())
                    .maxKeys(20) // Limit to first 20 objects
                    .build();
            
            var result = storageService.getS3Client().listObjectsV2(listRequest);
            var objects = result.contents().stream()
                    .map(obj -> java.util.Map.of(
                        "key", obj.key(),
                        "size", obj.size(),
                        "lastModified", obj.lastModified().toString()
                    ))
                    .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(java.util.Map.of(
                "bucket", storageService.getBucketName(),
                "totalObjects", result.keyCount(),
                "objects", objects
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(java.util.Map.of(
                "error", e.getMessage(),
                "bucket", "unknown"
            ));
        }
    }
    
    @GetMapping("/debug/network")
    public ResponseEntity<java.util.Map<String, Object>> networkDiagnostics() {
        java.util.Map<String, Object> diagnostics = new java.util.HashMap<>();
        
        try {
            // Test basic connectivity to Cloudflare R2
            diagnostics.put("timestamp", java.time.LocalDateTime.now().toString());
            diagnostics.put("bucketName", storageService.getBucketName());
            
            // Test DNS resolution
            try {
                java.net.URL endpointUrl = new java.net.URL(storageService.getS3Client().serviceClientConfiguration().endpointOverride().get().toString());
                java.net.InetAddress address = java.net.InetAddress.getByName(endpointUrl.getHost());
                diagnostics.put("dnsResolution", java.util.Map.of(
                    "hostname", endpointUrl.getHost(),
                    "resolved", true,
                    "ipAddress", address.getHostAddress()
                ));
            } catch (Exception e) {
                diagnostics.put("dnsResolution", java.util.Map.of(
                    "resolved", false,
                    "error", e.getMessage()
                ));
            }
            
            // Test R2 connectivity
            try {
                var listRequest = software.amazon.awssdk.services.s3.model.ListObjectsV2Request.builder()
                        .bucket(storageService.getBucketName())
                        .maxKeys(1)
                        .build();
                var result = storageService.getS3Client().listObjectsV2(listRequest);
                diagnostics.put("r2Connectivity", java.util.Map.of(
                    "connected", true,
                    "objectCount", result.keyCount(),
                    "responseTime", "OK"
                ));
            } catch (Exception e) {
                diagnostics.put("r2Connectivity", java.util.Map.of(
                    "connected", false,
                    "error", e.getClass().getSimpleName(),
                    "message", e.getMessage()
                ));
            }
            
            // System network info
            try {
                diagnostics.put("systemInfo", java.util.Map.of(
                    "javaVersion", System.getProperty("java.version"),
                    "osName", System.getProperty("os.name"),
                    "localHost", java.net.InetAddress.getLocalHost().getHostName()
                ));
            } catch (Exception e) {
                diagnostics.put("systemInfo", java.util.Map.of("error", e.getMessage()));
            }
            
            return ResponseEntity.ok(diagnostics);
            
        } catch (Exception e) {
            diagnostics.put("overallError", e.getMessage());
            return ResponseEntity.status(500).body(diagnostics);
        }
    }

    @GetMapping("/{key:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String key) {
        try {
            // Log the exact key received for debugging
            System.out.println("=== IMAGE REQUEST DEBUG ===");
            System.out.println("Raw key from path: [" + key + "]");
            
            // Try to decode the key in case it's still encoded
            String decodedKey = key;
            try {
                decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);
                if (!decodedKey.equals(key)) {
                    System.out.println("Decoded key: [" + decodedKey + "]");
                }
            } catch (Exception e) {
                System.out.println("Key decode not needed or failed, using original");
            }
            
            System.out.println("Final key for R2: [" + decodedKey + "]");
            System.out.println("Key length: " + decodedKey.length());
            System.out.println("=========================");
            
            byte[] imageData = storageService.downloadFile(decodedKey);

            // Determine content type from file extension
            String contentType = "application/octet-stream"; // Default
            try {
                Path path = Paths.get(decodedKey);
                contentType = Files.probeContentType(path);
                if (contentType == null) {
                    // Fallback for common types if probe fails
                    if (decodedKey.endsWith(".png")) contentType = "image/png";
                    else if (decodedKey.endsWith(".jpg") || decodedKey.endsWith(".jpeg")) contentType = "image/jpeg";
                    else if (decodedKey.endsWith(".gif")) contentType = "image/gif";
                    else if (decodedKey.endsWith(".webp")) contentType = "image/webp";
                }
            } catch (IOException e) {
                // Could not determine content type, use default
                System.err.println("Could not determine content type for key: " + decodedKey);
            }

            System.out.println("Successfully fetched image, size: " + imageData.length + " bytes");
            
            // Add caching and network-friendly headers
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header("Cache-Control", "public, max-age=31536000") // Cache for 1 year
                    .header("Access-Control-Allow-Origin", "*") // Explicit CORS for images
                    .header("Access-Control-Allow-Methods", "GET, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .header("Cross-Origin-Resource-Policy", "cross-origin") // Allow cross-origin loading
                    .header("X-Content-Type-Options", "nosniff")
                    .body(imageData);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("=== IMAGE FETCH ERROR ===");
            System.err.println("Failed to fetch image with key: [" + key + "]");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            System.err.println("Stack trace: ");
            e.printStackTrace();
            System.err.println("=========================");
            
            // Check for specific network-related errors
            String errorMessage = e.getMessage();
            if (errorMessage != null) {
                // DNS resolution issues
                if (errorMessage.contains("UnknownHostException") || 
                    errorMessage.contains("Name or service not known") ||
                    errorMessage.contains("No such host is known")) {
                    System.err.println("DNS ISSUE DETECTED: Cannot resolve Cloudflare R2 hostname");
                    return ResponseEntity.status(503) // Service Unavailable
                        .header("X-Error-Type", "DNS_RESOLUTION_FAILED")
                        .header("X-Error-Message", "Cannot resolve storage service hostname - check DNS settings")
                        .build();
                }
                
                // Connection timeout/refused
                if (errorMessage.contains("Connection timed out") || 
                    errorMessage.contains("Connection refused") ||
                    errorMessage.contains("ConnectException")) {
                    System.err.println("CONNECTION ISSUE DETECTED: Cannot connect to Cloudflare R2");
                    return ResponseEntity.status(503) // Service Unavailable
                        .header("X-Error-Type", "CONNECTION_FAILED")
                        .header("X-Error-Message", "Cannot connect to storage service - check firewall/network")
                        .build();
                }
                
                // SSL/TLS issues
                if (errorMessage.contains("SSLException") || 
                    errorMessage.contains("certificate") ||
                    errorMessage.contains("SSL")) {
                    System.err.println("SSL ISSUE DETECTED: SSL/TLS connection failed");
                    return ResponseEntity.status(503) // Service Unavailable
                        .header("X-Error-Type", "SSL_ERROR")
                        .header("X-Error-Message", "SSL/TLS connection failed - check certificate trust")
                        .build();
                }
            }
            
            // If image fetch fails for other reasons, return 404
            // This allows the frontend to fall back to placeholder
            return ResponseEntity.notFound()
                .header("X-Error-Type", "IMAGE_NOT_FOUND")
                .header("X-Error-Message", "Image not found or storage service error")
                .build();
        }
    }
}
