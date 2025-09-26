package com.aditi.dripyard.controller;

<<<<<<< HEAD
=======

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
import com.aditi.dripyard.domain.USER_ROLE;
import com.aditi.dripyard.exception.SellerException;
import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.VerificationCode;
import com.aditi.dripyard.request.LoginRequest;
import com.aditi.dripyard.request.SignupRequest;
import com.aditi.dripyard.response.ApiResponse;
import com.aditi.dripyard.response.AuthResponse;
import com.aditi.dripyard.service.AuthService;
<<<<<<< HEAD
=======
import jakarta.mail.MessagingException;
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.*;
=======
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

<<<<<<< HEAD
    @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<ApiResponse> sentLoginOtp(
            @RequestBody VerificationCode req) throws UserException {
        try {
            authService.sentLoginOtp(req.getEmail());
            ApiResponse res = new ApiResponse();
            res.setMessage("otp sent");
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new UserException("Failed to send OTP: " + e.getMessage());
        }
=======

    @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<ApiResponse> sentLoginOtp(
            @RequestBody VerificationCode req) throws MessagingException, UserException {

        authService.sentLoginOtp(req.getEmail());

        ApiResponse res = new ApiResponse();
        res.setMessage("otp sent");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(
<<<<<<< HEAD
            @Valid @RequestBody SignupRequest req) throws SellerException {
=======
            @Valid
            @RequestBody SignupRequest req)
            throws SellerException {


>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
        String token = authService.createUser(req);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Register Success");
        authResponse.setRole(USER_ROLE.ROLE_CUSTOMER);
<<<<<<< HEAD
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(
            @RequestBody LoginRequest loginRequest) throws SellerException {
        AuthResponse authResponse = authService.signin(loginRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
=======

        return new ResponseEntity<>(authResponse, HttpStatus.OK);

    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) throws SellerException {

        AuthResponse authResponse = authService.signin(loginRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }




>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
}
