// src/main/java/com/aditi/dripyard/service/impl/PaymentServiceImpl.java
package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.model.Order;
import com.aditi.dripyard.model.PaymentDetails;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.repository.OrderRepository;
import com.aditi.dripyard.response.PaymentLinkResponse;
import com.aditi.dripyard.service.PaymentService;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    @Value("${app.public.base-url:http://localhost:8080}")
    private String appBaseUrl;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public PaymentLinkResponse createRazorpayPaymentLink(Order order) throws RazorpayException {
        // Extract required info from the order object
        User user = order.getUser();
        Long amount = order.getTotalSellingPrice().longValue() * 100; // Amount in paise
        Long orderId = order.getId();

        try {
            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", amount);
            paymentLinkRequest.put("currency", "INR");

            JSONObject customer = new JSONObject();
            customer.put("name", user.getFullName());
            customer.put("email", user.getEmail());
            paymentLinkRequest.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);

            paymentLinkRequest.put("reminder_enable", true);

            // Reference the backend order so webhooks can map back reliably
            paymentLinkRequest.put("reference_id", String.valueOf(orderId));

            // Redirect back to backend success endpoint (configurable base URL)
            paymentLinkRequest.put("callback_url", appBaseUrl + "/api/payments/" + orderId + "/success");
            paymentLinkRequest.put("callback_method", "get");

            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

            String paymentLinkUrl = payment.get("short_url");
            String paymentLinkId = payment.get("id");
            String paymentLinkStatus = payment.get("status");

            // Persist payment link metadata on the order for webhook correlation
            PaymentDetails pd = order.getPaymentDetails();
            pd.setRazorPaymentLinkId(paymentLinkId);
            pd.setRazorPaymentLinkReferenceId(String.valueOf(orderId));
            pd.setRazorPaymentLinkStatus(paymentLinkStatus);
            orderRepository.save(order);

            PaymentLinkResponse res = new PaymentLinkResponse();
            res.setPayment_link_url(paymentLinkUrl);
            res.setPayment_link_id(paymentLinkId);

            return res;

        } catch (RazorpayException e) {
            System.out.println("Error creating Razorpay payment link: " + e.getMessage());
            throw new RazorpayException(e.getMessage());
        }
    }
}
