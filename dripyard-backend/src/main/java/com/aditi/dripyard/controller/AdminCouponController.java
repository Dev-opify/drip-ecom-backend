package com.aditi.dripyard.controller;


import com.aditi.dripyard.model.Cart;
import com.aditi.dripyard.model.Coupon;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.service.CartService;
import com.aditi.dripyard.service.CouponService;
import com.aditi.dripyard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class AdminCouponController {
    private final CouponService couponService;
    private final UserService userService;
    private final CartService cartService;

    @PostMapping("/apply")
    public ResponseEntity<Cart> applyCoupon(
            @RequestParam String apply,
            @RequestParam String code,
            @RequestParam double orderValue,
            @RequestHeader("Authorization"
            ) String jwt
    ) throws Exception {
        User user=userService.findUserProfileByJwt(jwt);
        Cart cart;

        if(apply.equals("true")){
            cart = couponService.applyCoupon(code,orderValue,user);
        }
        else {
            cart = couponService.removeCoupon(code,user);
        }

        return ResponseEntity.ok(cart);

    }


    // Admin operations

    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Coupon> createCoupon(
            @RequestBody Coupon coupon,
            @RequestHeader("Authorization") String jwt) throws Exception {
        
        // Verify user is admin
        User user = userService.findUserProfileByJwt(jwt);
        
        Coupon createdCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.ok(createdCoupon);
    }

    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCoupon(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) throws Exception {
        
        // Verify user is admin
        User user = userService.findUserProfileByJwt(jwt);
        
        couponService.deleteCoupon(id);
        return ResponseEntity.ok("Coupon deleted successfully");
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Coupon>> getAllCoupons(
            @RequestHeader("Authorization") String jwt) throws Exception {
        
        // Verify user is admin
        User user = userService.findUserProfileByJwt(jwt);
        
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }
}

