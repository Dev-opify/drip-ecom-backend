// dripyard-backend/src/main/java/com/aditi/dripyard/repository/ProductRepository.java
package com.aditi.dripyard.repository;

import com.aditi.dripyard.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // Changed from findBySellerId to findByUserId
    List<Product> findByUserId(Long userId);

    @Query("SELECT p FROM Product p WHERE (:query IS NULL OR LOWER(p.title) " +
            "LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "OR (:query IS NULL OR LOWER(p.category.name) " +
            "LIKE LOWER(CONCAT('%', :query, '%')))" +
            "OR (:query IS NULL OR LOWER(p.category.categoryId) " +
            "LIKE LOWER(CONCAT('%', :query, '%')))"
    )
    List<Product> searchProduct(@Param("query") String query);
}