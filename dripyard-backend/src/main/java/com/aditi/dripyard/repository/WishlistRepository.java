package com.aditi.dripyard.repository;


import com.aditi.dripyard.model.Wishlist;
import com.aditi.dripyard.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Wishlist findByUserId(Long userId);
    
    @Query("SELECT w FROM Wishlist w JOIN w.products p WHERE p.id = :productId")
    List<Wishlist> findWishlistsContainingProduct(@Param("productId") Long productId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM wishlist_products WHERE products_id = :productId", nativeQuery = true)
    void removeProductFromAllWishlists(@Param("productId") Long productId);
}
