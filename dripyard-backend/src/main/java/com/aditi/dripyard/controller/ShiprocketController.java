// dripyard-backend/src/main/java/com/aditi/dripyard/controller/ShiprocketController.java
package com.aditi.dripyard.controller;

import com.aditi.dripyard.dto.ShiprocketOrderRequestDto;
import com.aditi.dripyard.dto.ShiprocketOrderResponseDto;
import com.aditi.dripyard.service.ShiprocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shiprocket")
public class ShiprocketController {

    @Autowired
    private ShiprocketService shiprocketService;

    @PostMapping("/auth")
    public String authenticate(@RequestParam String email, @RequestParam String password) {
        return shiprocketService.authenticate(email, password);
    }

    @PostMapping("/order")
    public ShiprocketOrderResponseDto createOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody ShiprocketOrderRequestDto orderRequest
    ) throws Exception {
        return shiprocketService.createOrder(token.replace("Bearer ", ""), orderRequest);
    }
}
