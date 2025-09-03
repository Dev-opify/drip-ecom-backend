package com.aditi.dripyard.repository;

import com.aditi.dripyard.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Seller findByEmail(String email);



}
