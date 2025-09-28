package com.aditi.dripyard.controller;

import com.aditi.dripyard.domain.USER_ROLE;
import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.VerificationCode;
import com.aditi.dripyard.request.LoginRequest;
import com.aditi.dripyard.request.SignupRequest;
import com.aditi.dripyard.response.ApiResponse;
import com.aditi.dripyard.response.AuthResponse;
import com.aditi.dripyard.service.AuthService;
import com.mailersend.sdk.exceptions.MailerSendException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<ApiResponse> sentLoginOtp(@RequestBody VerificationCode req) throws UserException, MailerSendException {
        authService.sentLoginOtp(req.getEmail());
        ApiResponse res = new ApiResponse("OTP sent", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@Valid @RequestBody SignupRequest req) throws UserException { // Changed to UserException
        String token = authService.createUser(req);
        AuthResponse authResponse = new AuthResponse(token, true, "Register Success", USER_ROLE.ROLE_CUSTOMER);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) throws UserException { // Changed to UserException
        AuthResponse authResponse = authService.signin(loginRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
