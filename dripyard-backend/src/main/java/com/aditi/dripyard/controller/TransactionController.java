// dripyard-backend/src/main/java/com/aditi/dripyard/controller/TransactionController.java
package com.aditi.dripyard.controller;

import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.Transaction;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.service.TransactionService;
import com.aditi.dripyard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    // Endpoint for admins to view all transactions
    @GetMapping("/admin/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    // Endpoint for users to view their own transactions
    @GetMapping("/users/transactions")
    public ResponseEntity<List<Transaction>> getUsersTransactions(@RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        List<Transaction> transactions = transactionService.getUsersTransactions(user.getId());
        return ResponseEntity.ok(transactions);
    }
}