package com.aditi.dripyard.service;

import com.aditi.dripyard.exception.ProductException;
import com.aditi.dripyard.model.Product;
import com.aditi.dripyard.model.User; // Changed from Seller to User
import com.aditi.dripyard.request.CreateProductRequest;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ProductService {

    Product createProduct(CreateProductRequest req, User user) throws ProductException; // Signature updated

    void deleteProduct(Long productId) throws ProductException;

    Product updateProduct(Long productId, Product product) throws ProductException;

    Product updateProduct(Long productId, CreateProductRequest req) throws ProductException;

    Product updateProductStock(Long productId) throws ProductException;

    Product findProductById(Long id) throws ProductException;

    List<Product> searchProduct(String query);

    Page<Product> getAllProduct(String category, String brand, String colors, String sizes, Integer minPrice, Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize);

    List<Product> recentlyAddedProduct();

    List<Product> getProductsByAdmin(Long userId); // Renamed for clarity

    List<Product> getAllProducts(); // New method for admin

    void addImageToProduct(Long productId, String imageKey) throws ProductException;
    void removeImageFromProduct(Long productId, String imageKey) throws ProductException;
}
