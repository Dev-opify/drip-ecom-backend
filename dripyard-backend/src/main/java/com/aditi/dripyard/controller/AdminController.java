// dripyard-backend/src/main/java/com/aditi/dripyard/controller/AdminController.java
package com.aditi.dripyard.controller;

import com.aditi.dripyard.domain.OrderStatus;
import com.aditi.dripyard.exception.OrderException;
import com.aditi.dripyard.exception.ProductException;
import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.Order;
import com.aditi.dripyard.model.Product;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.request.CreateProductRequest;
import com.aditi.dripyard.response.ApiResponse;
import com.aditi.dripyard.service.OrderService;
import com.aditi.dripyard.service.ProductService;
import com.aditi.dripyard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest req, @RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        User admin = userService.findUserProfileByJwt(jwt);
        Product product = productService.createProduct(req, admin);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @GetMapping("/debug")
    public ResponseEntity<String> debugAuth(@RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<>("User: " + user.getEmail() + ", Role: " + user.getRole(), HttpStatus.OK);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) throws ProductException {
        productService.deleteProduct(productId);
        ApiResponse res = new ApiResponse("Product Deleted Successfully", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) throws OrderException {
        Order order = orderService.updateOrderStatus(orderId, status);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}