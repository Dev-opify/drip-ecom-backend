package com.aditi.dripyard.service.impl;


import com.aditi.dripyard.exception.WishlistNotFoundException;
import com.aditi.dripyard.model.Product;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.model.Wishlist;
import com.aditi.dripyard.repository.WishlistRepository;
import com.aditi.dripyard.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;


    @Override
    public Wishlist createWishlist(User user) {
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        return wishlistRepository.save(wishlist);
    }

    @Override
    public Wishlist getWishlistByUserId(User user) {
        Wishlist wishlist = wishlistRepository.findByUserId(user.getId());
        if (wishlist == null) {
            wishlist = this.createWishlist(user);
        }
        return wishlist;
    }

    @Override
    public Wishlist addProductToWishlist(User user, Product product) throws WishlistNotFoundException {
        Wishlist wishlist = this.getWishlistByUserId(user);
        if(wishlist.getProducts().contains(product)){
            wishlist.getProducts().remove(product);
        }
        else wishlist.getProducts().add(product);

        return wishlistRepository.save(wishlist);
    }

    @Override
    public Wishlist removeProductFromWishlist(User user, Product product) throws WishlistNotFoundException {
        Wishlist wishlist = this.getWishlistByUserId(user);
        wishlist.getProducts().removeIf(p -> p.getId().equals(product.getId()));
        return wishlistRepository.save(wishlist);
    }

}

