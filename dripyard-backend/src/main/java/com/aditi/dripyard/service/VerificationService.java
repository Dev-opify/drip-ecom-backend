package com.aditi.dripyard.service;

import com.zosh.model.VerificationCode;

public interface VerificationService {

    VerificationCode createVerificationCode(String otp, String email);
}
