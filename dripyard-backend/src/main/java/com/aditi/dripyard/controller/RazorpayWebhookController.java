package com.aditi.dripyard.controller;

import com.aditi.dripyard.domain.OrderStatus;
import com.aditi.dripyard.domain.PaymentStatus;
import com.aditi.dripyard.dto.ShiprocketOrderRequestDto;
import com.aditi.dripyard.model.Order;
import com.aditi.dripyard.service.EmailService;
import com.aditi.dripyard.service.OrderService;
import com.aditi.dripyard.service.ShiprocketService;
import com.aditi.dripyard.service.TransactionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Razorpay Webhook Controller
 * Handles payment.captured events and triggers automated order processing:
 * 1. Verify webhook signature (security)
 * 2. Update order status to PLACED
 * 3. Create Shiprocket shipping order
 * 4. Send confirmation email via MailerSend
 */
@Slf4j
@RestController
@RequestMapping("/api/webhooks/razorpay")
@RequiredArgsConstructor
public class RazorpayWebhookController {

    private final OrderService orderService;
    private final ShiprocketService shiprocketService;
    private final EmailService emailService;
    private final TransactionService transactionService;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @Value("${shiprocket.email}")
    private String shiprocketEmail;

    @Value("${shiprocket.password}")
    private String shiprocketPassword;

    @PostMapping("/payment-captured")
    public ResponseEntity<String> handlePaymentCaptured(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        log.info("Received Razorpay webhook: payment.captured");

        try {
            // Step 1: Verify webhook signature (CRITICAL for security)
            if (!verifyWebhookSignature(payload, signature)) {
                log.error("Webhook signature verification failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
            }

            // Step 2: Parse webhook payload
            ObjectMapper mapper = new ObjectMapper();
            JsonNode webhook = mapper.readTree(payload);
            
            String eventType = webhook.get("event").asText();
            if (!"payment.captured".equals(eventType)) {
                log.info("Ignoring event: {}", eventType);
                return ResponseEntity.ok("Event ignored");
            }

            // Step 3: Extract payment details
            JsonNode payment = webhook.get("payload").get("payment").get("entity");
            String razorpayPaymentId = payment.get("id").asText();
            String razorpayOrderId = payment.get("order_id").asText();
            
            log.info("Processing payment: {} for order: {}", razorpayPaymentId, razorpayOrderId);

            // Step 4: Find and update order in database
            Order order = orderService.findOrderByRazorpayOrderId(razorpayOrderId);
            if (order == null) {
                log.error("Order not found for Razorpay order ID: {}", razorpayOrderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
            }

            // Update order status and payment details
            order.getPaymentDetails().setPaymentId(razorpayPaymentId);
            order.getPaymentDetails().setStatus(PaymentStatus.COMPLETED);
            order.setOrderStatus(OrderStatus.PLACED);
            order = orderService.saveOrder(order);

            // Create transaction record
            transactionService.createTransaction(order);

            log.info("Order {} updated to PLACED status", order.getOrderId());

            // Step 5: Create Shiprocket order (automated shipping)
            try {
                createShiprocketOrder(order);
                log.info("Shiprocket order created for order: {}", order.getOrderId());
            } catch (Exception e) {
                log.error("Failed to create Shiprocket order for {}: {}", order.getOrderId(), e.getMessage());
                // Don't fail the webhook - order is still valid
            }

            // Step 6: Send confirmation email
            try {
                sendOrderConfirmationEmail(order);
                log.info("Confirmation email sent for order: {}", order.getOrderId());
            } catch (Exception e) {
                log.error("Failed to send confirmation email for {}: {}", order.getOrderId(), e.getMessage());
                // Don't fail the webhook - order is still valid
            }

            return ResponseEntity.ok("Payment processed successfully");

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing failed");
        }
    }

    /**
     * Verify Razorpay webhook signature for security
     */
    private boolean verifyWebhookSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString().equals(signature);
        } catch (Exception e) {
            log.error("Error verifying webhook signature: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Create Shiprocket order for shipping automation
     */
    private void createShiprocketOrder(Order order) throws Exception {
        // Authenticate with Shiprocket
        String shiprocketToken = shiprocketService.authenticate(shiprocketEmail, shiprocketPassword);
        
        // Extract token from response (assumes JSON response with "token" field)
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tokenResponse = mapper.readTree(shiprocketToken);
        String token = tokenResponse.get("token").asText();

        // Build Shiprocket order request
        ShiprocketOrderRequestDto shiprocketOrder = new ShiprocketOrderRequestDto();
        shiprocketOrder.setOrderId(order.getOrderId());
        shiprocketOrder.setOrderDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        shiprocketOrder.setPickupLocation("Primary"); // Configure in Shiprocket dashboard
        shiprocketOrder.setChannelId("5043317"); // Your channel ID

        // Set customer/billing details from order
        if (order.getShippingAddress() != null) {
            shiprocketOrder.setBillingCustomerName(order.getShippingAddress().getName());
            shiprocketOrder.setBillingAddress(order.getShippingAddress().getAddress());
            shiprocketOrder.setBillingCity(order.getShippingAddress().getCity());
            shiprocketOrder.setBillingState(order.getShippingAddress().getState());
            shiprocketOrder.setBillingCountry("India");
            shiprocketOrder.setBillingPincode(order.getShippingAddress().getPinCode());
            shiprocketOrder.setBillingPhone(order.getShippingAddress().getMobile());
        }

        // Create order in Shiprocket
        shiprocketService.createOrder(token, shiprocketOrder);
    }

    /**
     * Send order confirmation email via MailerSend
     */
    private void sendOrderConfirmationEmail(Order order) {
        String customerEmail = order.getUser().getEmail();
        String customerName = order.getUser().getFullName();
        String subject = "Order Confirmation - " + order.getOrderId();
        
        String emailContent = String.format(
            "Dear %s,\n\nThank you for your order!\n\nOrder ID: %s\nTotal Amount: ₹%s\n\nWe'll send you tracking details once your order ships.\n\nBest regards,\nDripYard Team",
            customerName,
            order.getOrderId(),
            order.getTotalSellingPrice()
        );

        emailService.sendEmail(customerEmail, subject, emailContent);
    }
}