package com.aditi.dripyard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.SdkHttpClient;

import java.time.Duration;

@Configuration
public class NetworkConfig {

    /**
     * Configure HTTP client with better timeout and retry settings
     * This helps with network connectivity issues across different WiFi networks
     */
    @Bean
    public SdkHttpClient httpClient() {
        return ApacheHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(30)) // Increased timeout for slower networks
                .socketTimeout(Duration.ofSeconds(60))     // Allow time for large image downloads
                .maxConnections(50)                        // Support concurrent requests
                .build();
    }
}