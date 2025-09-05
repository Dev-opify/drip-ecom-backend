package com.aditi.dripyard.service;



import com.aditi.dripyard.model.Order;
import com.aditi.dripyard.model.Seller;
import com.aditi.dripyard.model.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Order order);
    List<Transaction> getTransactionBySeller(Seller seller);
    List<Transaction>getAllTransactions();
}
