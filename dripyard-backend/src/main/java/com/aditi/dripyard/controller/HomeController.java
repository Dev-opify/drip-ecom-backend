package com.aditi.dripyard.controller;


import com.aditi.dripyard.response.ApiResponse;
import com.aditi.dripyard.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<ApiResponse> home(){
        ApiResponse apiResponse = new ApiResponse();
<<<<<<< HEAD
        apiResponse.setMessage("Dipyard Backend is running");
=======
        apiResponse.setMessage("Ecommerce  system");
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
        return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
    }}
