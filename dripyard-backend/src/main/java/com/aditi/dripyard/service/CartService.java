package com.aditi.dripyard.service;


import com.aditi.dripyard.exception.ProductException;
import com.aditi.dripyard.model.Cart;
import com.aditi.dripyard.model.CartItem;
import com.aditi.dripyard.model.Product;
import com.aditi.dripyard.model.User;

public interface CartService {
	
	public CartItem addCartItem(User user,
								Product product,
								String size,
								int quantity) throws ProductException;
	
	public Cart findUserCart(User user);

}
