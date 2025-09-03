package com.aditi.dripyard.controller;


import com.aditi.dripyard.domain.USER_ROLE;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.model.VerificationCode;
import com.aditi.dripyard.repository.UserRepository;
import com.aditi.dripyard.request.LoginRequest;
import com.aditi.dripyard.response.ApiResponse;
import com.aditi.dripyard.response.AuthResponse;
import com.aditi.dripyard.response.SignupRequest;
import com.aditi.dripyard.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest req) throws Exception {
        String jwt = authService.createUser(req);
        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setMessage("Signup successful");
        response.setRole(USER_ROLE.ROLE_CUSTOMER);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<ApiResponse> sentOtpHandler(@RequestBody VerificationCode req) throws Exception {
        authService.sentLoginOtp(req.getEmail());
        ApiResponse res = new ApiResponse();
        res.setMessage("Otp sent successfully ");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> loginHandler(@RequestBody LoginRequest req) throws Exception {
        AuthResponse authResponse = authService.signing(req);
        return ResponseEntity.ok(authResponse);
    }
}