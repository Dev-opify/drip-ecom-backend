package com.aditi.dripyard.service;

import com.aditi.dripyard.utils.EmailTemplate;
import com.mailersend.sdk.exceptions.MailerSendException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private MailerSendService mailerSendService;

    public void sendOrderConfirmationEmail(String to, String orderDetails) {
        try {
            String htmlContent = EmailTemplate.orderConfirmation(orderDetails);
            mailerSendService.sendEmail(to, "Order Confirmation - Dripyard", htmlContent);
        } catch (MailerSendException e) {
            throw new RuntimeException("Failed to send order confirmation email", e);
        }
    }

    public void sendPasswordResetEmail(String to, String resetToken) {
        try {
            String htmlContent = EmailTemplate.passwordReset(resetToken);
            mailerSendService.sendEmail(to, "Password Reset - Dripyard", htmlContent);
        } catch (MailerSendException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendWelcomeEmail(String to, String username) {
        try {
            String htmlContent = EmailTemplate.welcomeEmail(username);
            mailerSendService.sendEmail(to, "Welcome to Dripyard!", htmlContent);
        } catch (MailerSendException e) {
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }
}
