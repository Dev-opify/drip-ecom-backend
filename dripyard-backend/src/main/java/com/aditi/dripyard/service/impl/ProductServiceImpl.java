package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.exception.ProductException;
import com.aditi.dripyard.model.Category;
import com.aditi.dripyard.model.Product;
import com.aditi.dripyard.model.User; // Changed from Seller to User
import com.aditi.dripyard.repository.CategoryRepository;
import com.aditi.dripyard.repository.ProductRepository;
import com.aditi.dripyard.repository.WishlistRepository;
import com.aditi.dripyard.repository.CartItemRepository;
import com.aditi.dripyard.model.Wishlist;
import com.aditi.dripyard.model.CartItem;
import com.aditi.dripyard.request.CreateProductRequest;
import com.aditi.dripyard.service.ProductService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final WishlistRepository wishlistRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Product createProduct(CreateProductRequest req, User user) throws ProductException {
        // This logic is correct for the new single-vendor (admin) system.
        int discountPercentage = calculateDiscountPercentage(req.getMrpPrice(), req.getSellingPrice());

        // Extract category names from the nested structure
        String category1Name = req.getCategory() != null ? req.getCategory().getName() : null;
        String category2Name = req.getCategory() != null && req.getCategory().getParentCategory() != null 
                ? req.getCategory().getParentCategory().getName() : null;
        String category3Name = req.getCategory() != null && req.getCategory().getParentCategory() != null 
                && req.getCategory().getParentCategory().getParentCategory() != null
                ? req.getCategory().getParentCategory().getParentCategory().getName() : null;

        // Create or find category hierarchy
        Category topCategory = null;
        if (category3Name != null && !category3Name.isEmpty()) {
            String category3Id = category3Name.replace(" ", "_");
            topCategory = categoryRepository.findByCategoryId(category3Id);
            if (topCategory == null) {
                Category category = new Category();
                category.setCategoryId(category3Id);
                category.setLevel(1);
                category.setName(category3Name);
                topCategory = categoryRepository.save(category);
            }
        }

        Category midCategory = null;
        if (category2Name != null && !category2Name.isEmpty()) {
            String category2Id = category2Name.replace(" ", "_");
            midCategory = categoryRepository.findByCategoryId(category2Id);
            if (midCategory == null) {
                Category category = new Category();
                category.setCategoryId(category2Id);
                category.setLevel(2);
                category.setParentCategory(topCategory);
                category.setName(category2Name);
                midCategory = categoryRepository.save(category);
            }
        }

        Category finalCategory = null;
        if (category1Name != null && !category1Name.isEmpty()) {
            String category1Id = category1Name.replace(" ", "_");
            finalCategory = categoryRepository.findByCategoryId(category1Id);
            if (finalCategory == null) {
                Category category = new Category();
                category.setCategoryId(category1Id);
                category.setLevel(3);
                category.setParentCategory(midCategory != null ? midCategory : topCategory);
                category.setName(category1Name);
                finalCategory = categoryRepository.save(category);
            }
        }

        Product product = new Product();
        product.setUser(user); // Set the user (admin) as the owner
        product.setCategory(finalCategory);
        product.setTitle(req.getTitle());
        product.setColor(req.getColor());
        product.setDescription(req.getDescription());
        product.setDiscountPercent(discountPercentage);
        product.setSellingPrice(req.getSellingPrice());
        product.setImages(req.getImages() != null ? req.getImages() : new ArrayList<>());
        product.setMrpPrice(req.getMrpPrice());
        product.setSizes(req.getSizes());
        product.setCreatedAt(LocalDateTime.now());
        product.setQuantity(req.getQuantity() > 0 ? req.getQuantity() : 100); // Use provided quantity or default

        return productRepository.save(product);
    }

    // ... (calculateDiscountPercentage, addImageToProduct, deleteProduct, updateProduct, etc. remain the same)

    @Override
    public List<Product> getProductsByAdmin(Long userId) {
        return productRepository.findByUserId(userId);
    }

    // --- NEW METHOD ---
    // Added for the AdminController to fetch all products for management.
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // The rest of the methods remain the same...

    public static int calculateDiscountPercentage(double mrpPrice, double sellingPrice) {
        if (mrpPrice <= 0) {
            throw new IllegalArgumentException("Actual price must be greater than zero.");
        }
        double discount = mrpPrice - sellingPrice;
        double discountPercentage = (discount / mrpPrice) * 100;
        return (int) discountPercentage;
    }


    @Override
    public void addImageToProduct(Long productId, String imageKey) throws ProductException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("Product not found"));
        List<String> images = product.getImages();
        if (images == null) {
            images = new ArrayList<>();
        }
        // Avoid duplicates
        if (!images.contains(imageKey)) {
            images.add(imageKey);
            product.setImages(images);
            productRepository.save(product);
        }
    }

    @Override
    public void removeImageFromProduct(Long productId, String imageKey) throws ProductException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("Product not found"));
        List<String> images = product.getImages();
        if (images != null && images.remove(imageKey)) {
            product.setImages(images);
            productRepository.save(product);
        }
    }


    @Override
    public void deleteProduct(Long productId) throws ProductException {
        Product product = findProductById(productId);
        
        try {
            System.out.println("Starting product deletion process for product ID: " + productId);
            
            // Remove product from all wishlists first
            removeProductFromAllWishlists(productId);
            System.out.println("Product removed from wishlists");
            
            // Remove product from all carts
            removeProductFromAllCarts(productId);
            System.out.println("Product removed from carts");
            
            // Try to delete the product
            productRepository.delete(product);
            System.out.println("Product successfully deleted from database");
            
        } catch (Exception e) {
            System.err.println("Error deleting product " + productId + ": " + e.getMessage());
            
            // Check if it's a foreign key constraint error (product is in order history)
            String errorMessage = e.getMessage();
            if (errorMessage != null && (errorMessage.contains("foreign key constraint") || 
                                        errorMessage.contains("ConstraintViolation") ||
                                        errorMessage.contains("referenced"))) {
                
                // Product is in order history - do a soft delete instead
                System.out.println("Product is referenced in orders, performing soft delete (marking as out of stock)");
                product.setIn_stock(false);
                product.setQuantity(0);
                productRepository.save(product);
                
                // Throw a specific exception so frontend can inform the user
                throw new ProductException("Product marked as inactive and removed from store (preserved in order history)");
            } else {
                throw new ProductException("Failed to delete product: " + e.getMessage());
            }
        }
    }

    @Override
    public Product updateProduct(Long productId, Product product) throws ProductException {
        productRepository.findById(productId);
        product.setId(productId);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, CreateProductRequest req) throws ProductException {
        Product existingProduct = findProductById(productId);
        
        // Update basic fields
        existingProduct.setTitle(req.getTitle());
        existingProduct.setDescription(req.getDescription());
        existingProduct.setBrand(req.getBrand());
        existingProduct.setColor(req.getColor());
        existingProduct.setMrpPrice(req.getMrpPrice());
        existingProduct.setSellingPrice(req.getSellingPrice());
        existingProduct.setSizes(req.getSizes());
        existingProduct.setQuantity(req.getQuantity());
        
        // Recalculate discount percentage
        int discountPercentage = calculateDiscountPercentage(req.getMrpPrice(), req.getSellingPrice());
        existingProduct.setDiscountPercent(discountPercentage);
        
        // Update category if provided
        if (req.getCategory() != null) {
            String category1Name = req.getCategory().getName();
            String category2Name = req.getCategory().getParentCategory() != null 
                    ? req.getCategory().getParentCategory().getName() : null;
            String category3Name = req.getCategory().getParentCategory() != null 
                    && req.getCategory().getParentCategory().getParentCategory() != null
                    ? req.getCategory().getParentCategory().getParentCategory().getName() : null;

            // Create or find category hierarchy
            Category topCategory = null;
            if (category3Name != null && !category3Name.isEmpty()) {
                String category3Id = category3Name.replace(" ", "_");
                topCategory = categoryRepository.findByCategoryId(category3Id);
                if (topCategory == null) {
                    Category category = new Category();
                    category.setCategoryId(category3Id);
                    category.setLevel(1);
                    category.setName(category3Name);
                    topCategory = categoryRepository.save(category);
                }
            }

            Category midCategory = null;
            if (category2Name != null && !category2Name.isEmpty()) {
                String category2Id = category2Name.replace(" ", "_");
                midCategory = categoryRepository.findByCategoryId(category2Id);
                if (midCategory == null) {
                    Category category = new Category();
                    category.setCategoryId(category2Id);
                    category.setLevel(2);
                    category.setParentCategory(topCategory);
                    category.setName(category2Name);
                    midCategory = categoryRepository.save(category);
                }
            }

            Category finalCategory = null;
            if (category1Name != null && !category1Name.isEmpty()) {
                String category1Id = category1Name.replace(" ", "_");
                finalCategory = categoryRepository.findByCategoryId(category1Id);
                if (finalCategory == null) {
                    Category category = new Category();
                    category.setCategoryId(category1Id);
                    category.setLevel(3);
                    category.setParentCategory(midCategory != null ? midCategory : topCategory);
                    category.setName(category1Name);
                    finalCategory = categoryRepository.save(category);
                }
            }
            
            if (finalCategory != null) {
                existingProduct.setCategory(finalCategory);
            }
        }
        
        // Update images if provided
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            existingProduct.setImages(req.getImages());
        }
        
        return productRepository.save(existingProduct);
    }

    @Override
    public Product updateProductStock(Long productId) throws ProductException {
        Product product = this.findProductById(productId);
        product.setIn_stock(!product.isIn_stock());
        return productRepository.save(product);
    }

    @Override
    public Product findProductById(Long id) throws ProductException {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductException("product not found"));
    }

    @Override
    public List<Product> searchProduct(String query) {
        return productRepository.searchProduct(query);
    }

    @Override
    public Page<Product> getAllProduct(String category,
                                       String brand,
                                       String colors,
                                       String sizes,
                                       Integer minPrice,
                                       Integer maxPrice,
                                       Integer minDiscount,
                                       String sort,
                                       String stock,
                                       Integer pageNumber,
                                       Integer pageSize) {
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


            if (category != null) {
                Join<Product, Category> categoryJoin = root.join("category");
                Predicate categoryPredicate = criteriaBuilder.or(
                        criteriaBuilder.equal(categoryJoin.get("categoryId"), category),
                        criteriaBuilder.equal(categoryJoin.get("parentCategory").get("categoryId"), category)
                );


                predicates.add(categoryPredicate);
            }


            if (colors != null && !colors.isEmpty()) {
                System.out.println("color " + colors);
                predicates.add(criteriaBuilder.equal(root.get("color"), colors));
            }

            if (sizes != null && !sizes.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("size"), sizes));
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sellingPrice"),
                        minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("sellingPrice"),
                        maxPrice));
            }

            if (minDiscount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("discountPercent"),
                        minDiscount));
            }

            if (stock != null) {
                predicates.add(criteriaBuilder.equal(root.get("stock"), stock));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            pageable = switch (sort) {
                case "price_low" ->
                        PageRequest.of(pageNumber != null ? pageNumber : 0, pageSize, Sort.by("sellingPrice").ascending());
                case "price_high" ->
                        PageRequest.of(pageNumber != null ? pageNumber : 0, pageSize, Sort.by("sellingPrice").descending());
                default -> PageRequest.of(pageNumber != null ? pageNumber : 0, pageSize, Sort.unsorted());
            };
        } else {
            pageable = PageRequest.of(pageNumber != null ? pageNumber : 0, pageSize, Sort.unsorted());
        }


        return productRepository.findAll(spec, pageable);
    }

    @Override
    public List<Product> recentlyAddedProduct() {
        return List.of();
    }
    
    // Helper method to remove product from all wishlists
    private void removeProductFromAllWishlists(Long productId) {
        try {
            wishlistRepository.removeProductFromAllWishlists(productId);
        } catch (Exception e) {
            System.err.println("Error removing product from wishlists: " + e.getMessage());
            // If native query fails, try alternative approach
            try {
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    List<Wishlist> wishlists = wishlistRepository.findWishlistsContainingProduct(productId);
                    for (Wishlist wishlist : wishlists) {
                        wishlist.getProducts().remove(product);
                        wishlistRepository.save(wishlist);
                    }
                }
            } catch (Exception fallbackError) {
                System.err.println("Fallback wishlist cleanup also failed: " + fallbackError.getMessage());
            }
        }
    }
    
    // Helper method to remove product from all carts
    private void removeProductFromAllCarts(Long productId) {
        try {
            cartItemRepository.deleteByProductId(productId);
        } catch (Exception e) {
            System.err.println("Error removing product from carts: " + e.getMessage());
            // If JPQL query fails, try alternative approach
            try {
                List<CartItem> cartItems = cartItemRepository.findByProductId(productId);
                cartItemRepository.deleteAll(cartItems);
            } catch (Exception fallbackError) {
                System.err.println("Fallback cart cleanup also failed: " + fallbackError.getMessage());
            }
        }
    }
}
