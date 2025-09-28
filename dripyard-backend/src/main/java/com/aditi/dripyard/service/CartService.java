// dripyard-backend/src/main/java/com/aditi/dripyard/service/CartService.java
package com.aditi.dripyard.service;

import com.aditi.dripyard.exception.ProductException;
import com.aditi.dripyard.model.Cart;
import com.aditi.dripyard.model.CartItem;
import com.aditi.dripyard.model.Product;
import com.aditi.dripyard.model.User;

public interface CartService {

	CartItem addCartItem(User user, Product product, String size, int quantity) throws ProductException;

	Cart findUserCart(User user);

	// New method to clear the cart after an order is placed
	void clearCart(Long userId);
}