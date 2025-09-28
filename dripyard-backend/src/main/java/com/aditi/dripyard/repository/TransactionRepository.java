// dripyard-backend/src/main/java/com/aditi/dripyard/repository/TransactionRepository.java
package com.aditi.dripyard.repository;

import com.aditi.dripyard.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // The findBySellerId method has been removed as the 'seller' field no longer exists in Transaction.

    List<Transaction> findByCustomerId(Long customerId);
}