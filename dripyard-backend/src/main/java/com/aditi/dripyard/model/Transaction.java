// dripyard-backend/src/main/java/com/aditi/dripyard/model/Transaction.java
package com.aditi.dripyard.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User customer;

    @OneToOne
    private Order order;

    // The 'seller' field has been removed.

    private LocalDateTime date = LocalDateTime.now();
}