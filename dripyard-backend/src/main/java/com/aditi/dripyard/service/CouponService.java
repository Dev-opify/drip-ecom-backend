package com.aditi.dripyard.service;



import com.aditi.dripyard.model.Cart;
import com.aditi.dripyard.model.Coupon;
import com.aditi.dripyard.model.User;

import java.util.List;

public interface CouponService {
    Cart applyCoupon(String code, double orderValue, User user) throws Exception;
    Cart removeCoupon(String code, User user) throws Exception;
    Coupon createCoupon(Coupon coupon);
    void deleteCoupon(Long couponId);
    List<Coupon> getAllCoupons();
    
    Coupon getCouponById(Long couponId);
}
