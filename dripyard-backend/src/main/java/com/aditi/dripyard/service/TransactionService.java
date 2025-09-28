// dripyard-backend/src/main/java/com/aditi/dripyard/service/TransactionService.java
package com.aditi.dripyard.service;

import com.aditi.dripyard.model.Order;
import com.aditi.dripyard.model.Transaction;
import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Order order);

    List<Transaction> getAllTransactions();

    List<Transaction> getUsersTransactions(Long userId);
}