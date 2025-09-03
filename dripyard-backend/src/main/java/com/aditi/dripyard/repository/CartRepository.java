package com.aditi.dripyard.repository;

import com.aditi.dripyard.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {


}
