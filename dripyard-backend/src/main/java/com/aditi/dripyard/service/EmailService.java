package com.aditi.dripyard.service;

<<<<<<< HEAD
import com.aditi.dripyard.utils.EmailTemplate;
import com.mailersend.sdk.exceptions.MailerSendException;
import org.springframework.beans.factory.annotation.Autowired;
=======

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
<<<<<<< HEAD
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
=======
    private JavaMailSender javaMailSender;


    public void sendVerificationOtpEmail(String userEmail, String otp, String subject, String text) throws MessagingException, MailSendException {


        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");


            helper.setSubject(subject);
            helper.setText(text+otp, true);
            helper.setTo(userEmail);
            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new MailSendException("Failed to send email");
        }
    }
}

>>>>>>> 0f3c1a0e4673939a9872b19361bc1a6b1f00516c
