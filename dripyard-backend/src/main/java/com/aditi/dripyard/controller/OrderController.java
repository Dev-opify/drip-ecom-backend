// dripyard-backend/src/main/java/com/aditi/dripyard/controller/OrderController.java
package com.aditi.dripyard.controller;

import com.aditi.dripyard.exception.OrderException;
import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.*;
import com.aditi.dripyard.response.PaymentLinkResponse;
import com.aditi.dripyard.service.*;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final UserService userService;
	private final CartService cartService;
	private final PaymentService paymentService;

	@PostMapping("/")
	public ResponseEntity<PaymentLinkResponse> createOrderHandler(@RequestBody Address shippingAddress, @RequestHeader("Authorization") String jwt) throws UserException, RazorpayException {
		User user = userService.findUserProfileByJwt(jwt);
		Cart cart = cartService.findUserCart(user);
		Order order = orderService.createOrder(user, shippingAddress, cart);
		PaymentLinkResponse res = paymentService.createRazorpayPaymentLink(order);
		return new ResponseEntity<>(res, HttpStatus.CREATED);
	}

	@GetMapping("/user")
	public ResponseEntity<List<Order>> usersOrderHistoryHandler(@RequestHeader("Authorization") String jwt) throws UserException {
		User user = userService.findUserProfileByJwt(jwt);
		List<Order> orders = orderService.usersOrderHistory(user.getId());
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Order> findOrderById(@PathVariable("id") Long orderId, @RequestHeader("Authorization") String jwt) throws OrderException {
		// You can add validation here to ensure the user is an admin or owns the order
		Order order = orderService.findOrderById(orderId);
		return new ResponseEntity<>(order, HttpStatus.OK);
	}

	@PutMapping("/{orderId}/cancel")
	public ResponseEntity<Order> cancelOrderHandler(@PathVariable Long orderId, @RequestHeader("Authorization") String jwt) throws UserException, OrderException {
		User user = userService.findUserProfileByJwt(jwt);
		Order order = orderService.cancelOrder(orderId, user);
		return new ResponseEntity<>(order, HttpStatus.OK);
	}
}