package com.aditi.dripyard.service;


import com.aditi.dripyard.model.VerificationCode;

public interface VerificationService {

    VerificationCode createVerificationCode(String otp, String email);
}
