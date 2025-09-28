// src/main/java/com/aditi/dripyard/service/PaymentService.java
package com.aditi.dripyard.service;

import com.aditi.dripyard.model.Order;
import com.aditi.dripyard.response.PaymentLinkResponse;
import com.razorpay.RazorpayException;

public interface PaymentService {

    // Method signature is now corrected to accept an Order object
    PaymentLinkResponse createRazorpayPaymentLink(Order order) throws RazorpayException;

}