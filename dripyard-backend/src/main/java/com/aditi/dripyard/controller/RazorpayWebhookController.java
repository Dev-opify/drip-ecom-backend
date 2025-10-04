package com.aditi.dripyard.controller;

import com.aditi.dripyard.domain.OrderStatus;
import com.aditi.dripyard.domain.PaymentStatus;
import com.aditi.dripyard.dto.ShiprocketOrderRequestDto;
import com.aditi.dripyard.model.Order;
import com.aditi.dripyard.model.PaymentDetails;
import com.aditi.dripyard.service.MailerSendService;
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

@Slf4j
@RestController
@RequestMapping("/api/webhooks/razorpay")
@RequiredArgsConstructor
public class RazorpayWebhookController {

    private final OrderService orderService;
    private final TransactionService transactionService;
    private final ShiprocketService shiprocketService;
    private final MailerSendService mailerSendService;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @Value("${shiprocket.email}")
    private String shiprocketEmail;

    @Value("${shiprocket.password}")
    private String shiprocketPassword;

    @PostMapping("/payment")
    public ResponseEntity<String> handlePaymentWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            // Verify signature
            if (!verifySignature(payload, signature)) {
                log.error("Invalid Razorpay webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);
            String event = root.path("event").asText("");
            log.info("Razorpay webhook event: {}", event);

            // Prefer payment_link.paid for payment link flows
            if ("payment_link.paid".equals(event)) {
                JsonNode linkEntity = root.path("payload").path("payment_link").path("entity");
                String referenceId = linkEntity.path("reference_id").asText("");
                String paymentLinkId = linkEntity.path("id").asText("");
                String paymentId = root.path("payload").path("payment").path("entity").path("id").asText("");
                return processSuccessfulPayment(referenceId, paymentLinkId, paymentId);
            }
            // Fallback: handle payment.captured if present
            if ("payment.captured".equals(event)) {
                // If you add notes.reference_id when creating links, read here. For now, acknowledge.
                log.info("payment.captured received; ensure reference mapping exists if needed");
                return ResponseEntity.ok("captured acknowledged");
            }

            return ResponseEntity.ok("ignored");
        } catch (Exception e) {
            log.error("Error processing Razorpay webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }

    private ResponseEntity<String> processSuccessfulPayment(String referenceId, String paymentLinkId, String paymentId) {
        try {
            if (referenceId == null || referenceId.isEmpty()) {
                log.error("Missing reference_id in webhook");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("missing reference_id");
            }
            Order order = orderService.findOrderByPaymentLinkReferenceId(referenceId);
            if (order == null) {
                log.error("Order not found for reference_id: {}", referenceId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("order not found");
            }

            // Update order payment status and persist
            PaymentDetails pd = order.getPaymentDetails();
            pd.setPaymentId(paymentId);
            pd.setRazorPaymentLinkId(paymentLinkId != null && !paymentLinkId.isEmpty() ? paymentLinkId : pd.getRazorPaymentLinkId());
            pd.setRazorPaymentLinkStatus("paid");
            pd.setStatus(PaymentStatus.COMPLETED);

            order.setOrderStatus(OrderStatus.PLACED);
            orderService.saveOrder(order);

            // Create transaction record
            transactionService.createTransaction(order);

            // Create Shiprocket order (best-effort)
            try {
                String authJson = shiprocketService.authenticate(shiprocketEmail, shiprocketPassword);
                String token = new ObjectMapper().readTree(authJson).path("token").asText("");
                if (token != null && !token.isEmpty()) {
                    ShiprocketOrderRequestDto req = buildShiprocketOrder(order);
                    shiprocketService.createOrder(token, req);
                } else {
                    log.error("Shiprocket auth token empty");
                }
            } catch (Exception ex) {
                log.error("Shiprocket order creation failed: {}", ex.getMessage());
            }

            // Send confirmation email (best-effort)
            try {
                String to = order.getUser().getEmail();
                String subject = "Order Confirmation - " + order.getOrderId();
                String body = String.format(
                        "<p>Hi %s,</p><p>Thank you for your order!</p><p>Order ID: %s</p><p>Total: ₹%s</p><p>We'll email tracking details once shipped.</p>",
                        order.getUser().getFullName(), order.getOrderId(), order.getTotalSellingPrice());
                mailerSendService.sendEmail(to, subject, body);
            } catch (Exception ex) {
                log.error("Email sending failed: {}", ex.getMessage());
            }

            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            log.error("Error finalizing payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("finalization error");
        }
    }

    private ShiprocketOrderRequestDto buildShiprocketOrder(Order order) {
        ShiprocketOrderRequestDto dto = new ShiprocketOrderRequestDto();
        dto.setOrderId(order.getOrderId());
        dto.setOrderDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        dto.setPickupLocation("Primary");
        dto.setChannelId("ONLINE");
        if (order.getShippingAddress() != null) {
            dto.setBillingCustomerName(order.getShippingAddress().getName());
            dto.setBillingAddress(order.getShippingAddress().getAddress());
            dto.setBillingCity(order.getShippingAddress().getCity());
            dto.setBillingState(order.getShippingAddress().getState());
            dto.setBillingCountry("India");
            dto.setBillingPincode(order.getShippingAddress().getPinCode());
            dto.setBillingPhone(order.getShippingAddress().getMobile());
        }

        // Payment mode derived from successful Razorpay payment
        dto.setPaymentMethod("Prepaid");

        // Totals
        Integer subtotal = order.getTotalSellingPrice() != null ? order.getTotalSellingPrice() : 0;
        Integer discount = order.getDiscount() != null ? order.getDiscount() : 0;
        int shipping = (subtotal >= 800) ? 0 : 99; // mirrors frontend rule
        dto.setSubTotal(subtotal);
        dto.setDiscount(discount);
        dto.setShippingCharges(shipping);

        // Items and dimensions using in-house stacking rule
        int totalUnits = 0;
        if (order.getOrderItems() != null) {
            for (com.aditi.dripyard.model.OrderItem it : order.getOrderItems()) {
                com.aditi.dripyard.dto.ShiprocketOrderItemDto item = new com.aditi.dripyard.dto.ShiprocketOrderItemDto();
                String name = (it.getProduct() != null && it.getProduct().getTitle() != null) ? it.getProduct().getTitle() : "Item";
                String sku = (it.getProduct() != null && it.getProduct().getId() != null)
                        ? String.valueOf(it.getProduct().getId())
                        : ("ITEM-" + it.getId());
                int units = it.getQuantity();
                double price = (it.getSellingPrice() != null) ? it.getSellingPrice() : (it.getProduct() != null ? it.getProduct().getSellingPrice() : 0);

                item.setName(name);
                item.setSku(sku);
                item.setUnits(units);
                item.setSellingPrice(price);
                dto.getOrderItems().add(item);

                totalUnits += Math.max(units, 0);
            }
        }
        // Stacking rule: base 30x30 cm footprint, height increases by 5 cm per unit; weight 0.3 kg per unit
        if (totalUnits > 0) {
            dto.setLength(30.0);
            dto.setBreadth(30.0);
            dto.setHeight(5.0 * totalUnits);
            dto.setWeight(0.3 * totalUnits);
        }

        return dto;
    }

    private boolean verifySignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString().equals(signature);
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage());
            return false;
        }
    }
}