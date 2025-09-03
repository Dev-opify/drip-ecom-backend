package com.aditi.dripyard.service;

import com.aditi.dripyard.request.LoginRequest;
import com.aditi.dripyard.response.AuthResponse;
import com.aditi.dripyard.response.SignupRequest;
import jakarta.mail.MessagingException;

public interface AuthService {

void sentLoginOtp(String email) throws MessagingException;
    String createUser(SignupRequest req ) throws Exception;

    AuthResponse signing(LoginRequest req);


}
