package com.aditi.dripyard.model;

import com.aditi.dripyard.domain.PaymentMethod;
import com.aditi.dripyard.domain.PaymentOrderStatus;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long amount;

    private PaymentOrderStatus status = PaymentOrderStatus.PENDING;

    private PaymentMethod paymentMethod;

    private String paymentLink;


    @ManyToOne
    private User user;

    @OneToMany
    private Set<Order> orders = new HashSet<>();


}
