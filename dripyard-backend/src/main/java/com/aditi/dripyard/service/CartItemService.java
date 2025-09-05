package com.aditi.dripyard.service;


import com.aditi.dripyard.exception.CartItemException;
import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.CartItem;

public interface CartItemService {
	
	public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException;
	
	public void removeCartItem(Long userId,Long cartItemId) throws CartItemException, UserException;
	
	public CartItem findCartItemById(Long cartItemId) throws CartItemException;
	
}
