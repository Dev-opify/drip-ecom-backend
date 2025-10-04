package com.aditi.dripyard.service;


import com.aditi.dripyard.exception.WishlistNotFoundException;
import com.aditi.dripyard.model.Product;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.model.Wishlist;

public interface WishlistService {

    Wishlist createWishlist(User user);

    Wishlist getWishlistByUserId(User user);

    Wishlist addProductToWishlist(User user, Product product) throws WishlistNotFoundException;

    Wishlist removeProductFromWishlist(User user, Product product) throws WishlistNotFoundException;

}

