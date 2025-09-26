package com.aditi.dripyard.repository;

import com.aditi.dripyard.domain.AccountStatus;
import com.aditi.dripyard.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Seller findByEmail(String email);
    List<Seller> findByAccountStatus(AccountStatus status);


}
