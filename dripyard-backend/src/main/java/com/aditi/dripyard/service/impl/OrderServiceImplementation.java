package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.domain.OrderStatus;
import com.aditi.dripyard.domain.PaymentStatus;
import com.aditi.dripyard.exception.OrderException;
import com.aditi.dripyard.model.*;
import com.aditi.dripyard.repository.AddressRepository;
import com.aditi.dripyard.repository.OrderItemRepository;
import com.aditi.dripyard.repository.OrderRepository;
import com.aditi.dripyard.service.CartService;
import com.aditi.dripyard.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImplementation implements OrderService {

	private final OrderRepository orderRepository;
	private final AddressRepository addressRepository;
	private final OrderItemRepository orderItemRepository;
	private final CartService cartService;

	@Override
	public Order createOrder(User user, Address shippAddress, Cart cart) {
		// Save the shipping address first
		shippAddress = addressRepository.save(shippAddress);

		// Create a single order for the entire cart
		Order order = new Order();
		order.setUser(user);
		order.setShippingAddress(shippAddress);
		order.setOrderDate(LocalDateTime.now());
		order.setOrderStatus(OrderStatus.PLACED); // A more appropriate initial status
		order.getPaymentDetails().setStatus(PaymentStatus.PENDING);
		order.setOrderId(UUID.randomUUID().toString());

		// Persist the order first to get an ID for the OrderItems
		Order savedOrder = orderRepository.save(order);

		List<OrderItem> orderItems = new ArrayList<>();
		for (CartItem cartItem : cart.getCartItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setSize(cartItem.getSize());
			orderItem.setSellingPrice(cartItem.getSellingPrice());
			orderItem.setMrpPrice(cartItem.getMrpPrice());
			orderItem.setOrder(savedOrder); // Link to the saved order
			orderItems.add(orderItem);
		}

		// Save all order items
		orderItemRepository.saveAll(orderItems);

		// Set the final details on the order
		savedOrder.setOrderItems(orderItems);
		savedOrder.setTotalSellingPrice((int) cart.getTotalSellingPrice());
		savedOrder.setTotalMrpPrice((int) cart.getTotalMrpPrice());
		savedOrder.setTotalItem(cart.getTotalItem());
		savedOrder.setDiscount((int) cart.getDiscount());

		// Clear the user's cart after the order is created
		cartService.clearCart(user.getId());

		return orderRepository.save(savedOrder);
	}

	@Override
	public Order findOrderById(Long orderId) throws OrderException {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderException("Order not found with id " + orderId));
	}

	@Override
	public List<Order> usersOrderHistory(Long userId) {
		return orderRepository.findByUserId(userId);
	}

	@Override
	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Order updateOrderStatus(Long orderId, OrderStatus status) throws OrderException {
		Order order = findOrderById(orderId);
		order.setOrderStatus(status);
		return orderRepository.save(order);
	}

	@Override
	public void deleteOrder(Long orderId) throws OrderException {
		Order order = findOrderById(orderId);
		orderRepository.delete(order);
	}

	@Override
	public Order cancelOrder(Long orderId, User user) throws OrderException {
		Order order = findOrderById(orderId);
		if (!order.getUser().getId().equals(user.getId())) {
			throw new OrderException("You are not authorized to cancel this order.");
		}
		order.setOrderStatus(OrderStatus.CANCELLED);
		return orderRepository.save(order);
	}

	@Override
	public Order findOrderByRazorpayOrderId(String razorpayOrderId) {
		return orderRepository.findByPaymentDetailsRazorPaymentLinkReferenceId(razorpayOrderId);
	}

	@Override
	public Order saveOrder(Order order) {
		return orderRepository.save(order);
	}
}
