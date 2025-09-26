package com.aditi.dripyard.service;

import com.mailersend.sdk.exceptions.MailerSendException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final MailerSendService mailerSendService;

    public void sendVerificationOtpEmail(String userEmail, String otp, String subject, String text)
            throws MailerSendException {
        String emailContent = text + otp;
        mailerSendService.sendEmail(userEmail, subject, emailContent);
    }
}
