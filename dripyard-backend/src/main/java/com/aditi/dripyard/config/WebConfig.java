package com.aditi.dripyard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://127.0.0.1:5500", 
                    "http://localhost:3000",
                    "http://localhost:5173", 
                    "http://localhost:5174",
                    "https://dripyardwebsite.vercel.app",
                    "https://*.vercel.app"
                )
                .allowedOriginPatterns("*") // Allow all origins for development - restrict in production
                .allowedMethods("GET","POST","PUT","DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight response for 1 hour
    }
}
