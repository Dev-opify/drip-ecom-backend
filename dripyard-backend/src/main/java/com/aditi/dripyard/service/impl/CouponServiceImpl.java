package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.exception.CouponNotValidException;
import com.aditi.dripyard.model.Cart;
import com.aditi.dripyard.model.Coupon;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.repository.CartRepository;
import com.aditi.dripyard.repository.CouponRepository;
import com.aditi.dripyard.repository.UserRepository;
import com.aditi.dripyard.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    @Override
    public Cart applyCoupon(String code,
                            double orderValue,
                            User user)
            throws Exception {
        Coupon coupon = couponRepository.findByCode(code);
        Cart cart = cartRepository.findByUserId(user.getId());


        if (coupon==null) {
            throw new CouponNotValidException("Coupon not found");
        }
        
        // Don't check if coupon was used before - allow reuse in same session
        // Only check if ANOTHER coupon is currently applied to this cart
        if (cart.getCouponCode() != null && !cart.getCouponCode().equals(code)) {
            throw new CouponNotValidException("Remove current coupon before applying a new one");
        }
        
        // If same coupon is already applied, don't apply again
        if (cart.getCouponCode() != null && cart.getCouponCode().equals(code)) {
            throw new CouponNotValidException("This coupon is already applied");
        }
        
        // Check minimum order value (should be < not <=)
        if(orderValue < coupon.getMinimumOrderValue()){
            throw new CouponNotValidException("Minimum order value ₹" + coupon.getMinimumOrderValue() + " required");
        }
        
        // Check if coupon is active
        if (!coupon.isActive()) {
            throw new CouponNotValidException("Coupon is not active");
        }
        
        // Check validity dates
        LocalDate now = LocalDate.now();
        if (coupon.getValidityStartDate() != null && now.isBefore(coupon.getValidityStartDate())) {
            throw new CouponNotValidException("Coupon not yet valid");
        }
        if (coupon.getValidityEndDate() != null && now.isAfter(coupon.getValidityEndDate())) {
            throw new CouponNotValidException("Coupon has expired");
        }

        // All validations passed - apply coupon
        double discountedPrice = Math.round((cart.getTotalSellingPrice() * coupon.getDiscountPercentage()) / 100);
        cart.setCouponCode(code);
        cart.setCouponPrice((int) discountedPrice);
        
        // Note: We don't modify totalSellingPrice here - keep original price
        // Discount is stored separately in couponPrice
        
        return cartRepository.save(cart);

    }

    @Override
    public Cart removeCoupon(String code, User user) throws Exception {
        Cart cart = cartRepository.findByUserId(user.getId());
        
        // Check if any coupon is applied
        if(cart.getCouponCode() == null){
            throw new Exception("No coupon applied to remove");
        }
        
        // Check if the correct coupon code is being removed
        if(!cart.getCouponCode().equals(code)){
            throw new Exception("Coupon code does not match the applied coupon");
        }

        // Simply reset coupon fields - don't modify totalSellingPrice
        cart.setCouponCode(null);
        cart.setCouponPrice(0);
        return cartRepository.save(cart);

    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteCoupon(Long couponId) {
        // Find the coupon first
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + couponId));
        
        // Remove the coupon from all users who have used it
        // This prevents foreign key constraint violation
        if (!coupon.getUsedByUsers().isEmpty()) {
            for (User user : coupon.getUsedByUsers()) {
                user.getUsedCoupons().remove(coupon);
            }
            // Save all users to update the relationship
            userRepository.saveAll(coupon.getUsedByUsers());
            
            // Clear the set to break the relationship from coupon side
            coupon.getUsedByUsers().clear();
        }
        
        // Now we can safely delete the coupon
        couponRepository.delete(coupon);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    public Coupon getCouponById(Long couponId) {
//        return couponRepository.findById(couponId).orElseThrow(new Exception("coupon not found"));
        return null;
    }
}
