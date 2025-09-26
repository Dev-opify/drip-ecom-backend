package com.aditi.dripyard.controller;

import com.aditi.dripyard.service.MailerSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class MailerSendController {
    private final MailerSendService mailerSendService;

    @PostMapping(value = "/mail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestParam String to) {
        try {
            log.info("Sending email to: {}", to);
            mailerSendService.sendEmail(
                    to,
                    "Test Email from Dripyard",
                    "This is a test email sent via MailerSend"
            );

            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "Email sent successfully to " + to);

            log.info("Email sent successfully: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}

