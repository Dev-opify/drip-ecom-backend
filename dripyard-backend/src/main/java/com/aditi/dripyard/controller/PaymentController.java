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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;
    private final TransactionService transactionService;

    @GetMapping("/{orderId}/success")
    public ResponseEntity<ApiResponse> paymentSuccessRedirect(
            @PathVariable Long orderId,
            @RequestParam("razorpay_payment_id") String razorpayPaymentId
    ) throws OrderException {

        Order order = orderService.findOrderById(orderId);
        order.getPaymentDetails().setPaymentId(razorpayPaymentId);
        order.getPaymentDetails().setStatus(PaymentStatus.COMPLETED);
        order.setOrderStatus(com.aditi.dripyard.domain.OrderStatus.PLACED);

        // Create a transaction record for this successful order
        transactionService.createTransaction(order);

        ApiResponse res = new ApiResponse("Your Order is Placed Successfully", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}