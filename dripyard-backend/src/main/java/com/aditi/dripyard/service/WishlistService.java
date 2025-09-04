package com.aditi.dripyard.service;


import com.zosh.exception.WishlistNotFoundException;
import com.zosh.model.Product;
import com.zosh.model.User;
import com.zosh.model.Wishlist;

public interface WishlistService {

    Wishlist createWishlist(User user);

    Wishlist getWishlistByUserId(User user);

    Wishlist addProductToWishlist(User user, Product product) throws WishlistNotFoundException;

}

