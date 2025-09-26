package com.aditi.dripyard.service;

<<<<<<< HEAD
=======

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
import com.aditi.dripyard.exception.SellerException;
import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.request.LoginRequest;
import com.aditi.dripyard.request.SignupRequest;
import com.aditi.dripyard.response.AuthResponse;
<<<<<<< HEAD

public interface AuthService {
    void sentLoginOtp(String email) throws UserException;
    String createUser(SignupRequest req) throws SellerException;
    AuthResponse signin(LoginRequest req) throws SellerException;
}
=======
import jakarta.mail.MessagingException;

public interface AuthService {

    void sentLoginOtp(String email) throws UserException, MessagingException;
    String createUser(SignupRequest req) throws SellerException;
    AuthResponse signin(LoginRequest req) throws SellerException;

}

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
