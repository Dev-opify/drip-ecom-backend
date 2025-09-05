package com.aditi.dripyard.service;



import com.aditi.dripyard.domain.OrderStatus;
import com.aditi.dripyard.exception.OrderException;
import com.aditi.dripyard.model.Address;
import com.aditi.dripyard.model.Cart;
import com.aditi.dripyard.model.Order;
import com.aditi.dripyard.model.User;

import java.util.List;
import java.util.Set;

public interface OrderService {
	
	public Set<Order> createOrder(User user, Address shippingAddress, Cart cart);
	
	public Order findOrderById(Long orderId) throws OrderException;
	
	public List<Order> usersOrderHistory(Long userId);
	
	public List<Order>getShopsOrders(Long sellerId);

	public Order updateOrderStatus(Long orderId,
								   OrderStatus orderStatus)
			throws OrderException;
	
	public void deleteOrder(Long orderId) throws OrderException;

	Order cancelOrder(Long orderId,User user) throws OrderException;
	
}
