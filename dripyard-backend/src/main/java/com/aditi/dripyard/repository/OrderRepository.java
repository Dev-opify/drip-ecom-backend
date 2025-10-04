package com.aditi.dripyard.repository;

import com.aditi.dripyard.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order>findByUserId(Long userId);
    List<Order> findBySellerIdOrderByOrderDateDesc(Long sellerId);
    List<Order> findBySellerIdAndOrderDateBetween(Long sellerId,LocalDateTime startDate, LocalDateTime endDate);

    // Correlate Razorpay webhook using the payment link reference id we set to the backend order id
    Order findByPaymentDetailsRazorPaymentLinkReferenceId(String referenceId);
}
