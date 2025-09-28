// dripyard-backend/src/main/java/com/aditi/dripyard/service/impl/TransactionServiceImpl.java
package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.model.Order;
import com.aditi.dripyard.model.Transaction;
import com.aditi.dripyard.repository.TransactionRepository;
import com.aditi.dripyard.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction createTransaction(Order order) {
        Transaction transaction = new Transaction();
        transaction.setCustomer(order.getUser());
        transaction.setOrder(order);
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> getUsersTransactions(Long userId) {
        return transactionRepository.findByCustomerId(userId);
    }
}