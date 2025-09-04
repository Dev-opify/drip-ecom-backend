package com.aditi.dripyard.controller;

import com.aditi.dripyard.model.VerificationCode;
import com.aditi.dripyard.repository.VerificationCodeRepository;
import com.aditi.dripyard.request.LoginRequest;
import com.aditi.dripyard.response.AuthResponse;
import com.aditi.dripyard.service.AuthService;
import com.aditi.dripyard.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor


public class SellerController {
    private final SellerService sellerService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AuthService authService;


    @PostMapping( "/login")
    public ResponseEntity<AuthResponse> loginSeller(
            @RequestBody LoginRequest req
            ) throws Exception  {

        String otp = req.getOtp();
        String email = req.getEmail();
//        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);
//        if (verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())) {
//            throw new Exception("wrong otp...");
//        }
//

        req.setEmail("seller_"+ email);
        AuthResponse authResponse = authService.signing(req);

        return ResponseEntity.ok(authResponse);
    }



}
