// dripyard-backend/src/main/java/com/aditi/dripyard/controller/PaymentController.java
package com.aditi.dripyard.controller;

import com.aditi.dripyard.domain.PaymentStatus;
import com.aditi.dripyard.exception.OrderException;
import com.aditi.dripyard.model.Order;
import com.aditi.dripyard.repository.OrderRepository;
import com.aditi.dripyard.response.ApiResponse;
import com.aditi.dripyard.service.OrderService;
import com.aditi.dripyard.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;
    private final TransactionService transactionService;

    @Value("${app.frontend.base-url:https://dripyardwebsite.vercel.app}")
    private String frontendBaseUrl;

    @GetMapping("/{orderId}/success")
    public ResponseEntity<?> paymentSuccessRedirect(
            @PathVariable Long orderId,
            @RequestParam("razorpay_payment_id") String razorpayPaymentId
    ) throws OrderException {

        Order order = orderService.findOrderById(orderId);
        order.getPaymentDetails().setPaymentId(razorpayPaymentId);
        order.getPaymentDetails().setStatus(PaymentStatus.COMPLETED);
        order.setOrderStatus(com.aditi.dripyard.domain.OrderStatus.PLACED);

        // Persist order updates
        orderService.saveOrder(order);

        // Create a transaction record for this successful order
        transactionService.createTransaction(order);

        // Redirect to frontend payment success page with orderId
        String redirectUrl = String.format("%s/paymentSuccess/index.html?orderId=%d", frontendBaseUrl, orderId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, redirectUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}