// dripyard-backend/src/main/java/com/aditi/dripyard/service/OrderService.java
package com.aditi.dripyard.service;

import com.aditi.dripyard.domain.OrderStatus;
import com.aditi.dripyard.exception.OrderException;
import com.aditi.dripyard.model.Address;
import com.aditi.dripyard.model.Cart;
import com.aditi.dripyard.model.Order;
import com.aditi.dripyard.model.User;
import java.util.List;

public interface OrderService {

	// Corrected to return a single Order
	Order createOrder(User user, Address shippingAddress, Cart cart);

	Order findOrderById(Long orderId) throws OrderException;

	List<Order> usersOrderHistory(Long userId);

	// New method to get all orders for the admin dashboard
	List<Order> getAllOrders();

	Order updateOrderStatus(Long orderId, OrderStatus status) throws OrderException;

	void deleteOrder(Long orderId) throws OrderException;

	Order cancelOrder(Long orderId, User user) throws OrderException;

	// Correlate webhook by payment link reference id
	Order findOrderByPaymentLinkReferenceId(String referenceId);

	// Persist order changes (e.g., after payment success)
	Order saveOrder(Order order);
}
