package com.aditi.dripyard.service;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailerSendService {

    private final MailerSend mailerSend;
    private final String fromEmail;
    private final String fromName;
    private final String adminEmail;

    public MailerSendService(
            @Value("${mailersend.api.token}") String apiToken,
            @Value("${mailersend.from.email:${mailersend.admin.email}}") String fromEmail,
            @Value("${mailersend.from.name:Dripyard}") String fromName,
            @Value("${mailersend.admin.email}") String adminEmail) {
        this.mailerSend = new MailerSend();
        this.mailerSend.setToken(apiToken);
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.adminEmail = adminEmail;
    }

    public void sendEmail(String to, String subject, String text) throws MailerSendException {
        Email email = new Email();

        // Use the test domain for sending
        email.setFrom(fromEmail, fromName);
        email.addRecipient(to, to.split("@")[0]);
        email.setSubject(subject);
        email.setHtml(text);
        email.setPlain(text.replaceAll("<[^>]*>", ""));

        try {
            mailerSend.emails().send(email);
            log.info("Email sent successfully to: {}", to);
        } catch (MailerSendException e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage());
            throw e;
        }
    }
}
