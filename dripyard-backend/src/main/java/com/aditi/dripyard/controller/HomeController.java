package com.aditi.dripyard.controller;


import com.aditi.dripyard.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController


public class HomeController {
    @GetMapping




    public ApiResponse HomeControllerHandler() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Welcome to DripYard Backend");
         return apiResponse;
    }



}
