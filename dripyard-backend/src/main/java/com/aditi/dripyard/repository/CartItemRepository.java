package com.aditi.dripyard.repository;


import com.aditi.dripyard.model.Cart;
import com.aditi.dripyard.model.CartItem;
import com.aditi.dripyard.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {


    CartItem findByCartAndProductAndSize(Cart cart, Product product, String size);


}
