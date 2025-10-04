// dripyard-backend/src/main/java/com/aditi/dripyard/service/ShiprocketService.java
package com.aditi.dripyard.service;

import com.aditi.dripyard.dto.ShiprocketOrderRequestDto;
import com.aditi.dripyard.dto.ShiprocketOrderResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ShiprocketService {

    @Value("${shiprocket.api.key}")
    private String apiKey;

    @Value("${shiprocket.api.secret}")
    private String apiSecret;

    @Value("${shiprocket.api.baseurl}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String authenticate(String email, String password) {
        String url = baseUrl + "/auth/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    public ShiprocketOrderResponseDto createOrder(String token, ShiprocketOrderRequestDto orderRequest) throws Exception {
        String url = baseUrl + "/orders/create/adhoc";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        ObjectMapper mapper = new ObjectMapper();
        String orderJson = mapper.writeValueAsString(orderRequest);

        HttpEntity<String> request = new HttpEntity<>(orderJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // Map response to DTO
        return mapper.readValue(response.getBody(), ShiprocketOrderResponseDto.class);
    }
}
