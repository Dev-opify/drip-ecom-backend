package com.aditi.dripyard.service;

import com.aditi.dripyard.exception.SellerException;
import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.request.LoginRequest;
import com.aditi.dripyard.request.SignupRequest;
import com.aditi.dripyard.response.AuthResponse;
import com.mailersend.sdk.exceptions.MailerSendException;

public interface AuthService {

    void sentLoginOtp(String email) throws UserException, MailerSendException;

    String createUser(SignupRequest req) throws SellerException;

    AuthResponse signin(LoginRequest req) throws SellerException;
}
