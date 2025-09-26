package com.aditi.dripyard.model;


import com.aditi.dripyard.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PaymentDetails {
     private String paymentId;

     private String razorPaymentLinkId;
     private String razorPaymentLinkReferenceId;
     private String razorPaymentLinkStatus;
     private String razorPaymentIdZWSP;
     private PaymentStatus status;

}
