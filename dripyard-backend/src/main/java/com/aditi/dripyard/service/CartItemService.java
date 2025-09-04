package com.aditi.dripyard.service;

import com.zosh.exception.CartItemException;
import com.zosh.exception.UserException;
import com.zosh.model.CartItem;


public interface CartItemService {
	
	public CartItem updateCartItem(Long userId, Long id,CartItem cartItem) throws CartItemException, UserException;
	
	public void removeCartItem(Long userId,Long cartItemId) throws CartItemException, UserException;
	
	public CartItem findCartItemById(Long cartItemId) throws CartItemException;
	
}
