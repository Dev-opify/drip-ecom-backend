package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.exception.ProductException;
import com.aditi.dripyard.model.Category;
import com.aditi.dripyard.model.Product;
import com.aditi.dripyard.model.User; // Changed from Seller to User
import com.aditi.dripyard.repository.CategoryRepository;
import com.aditi.dripyard.repository.ProductRepository;
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

    @Override
    public Product createProduct(CreateProductRequest req, User user) throws ProductException {
        // This logic is correct for the new single-vendor (admin) system.
        int discountPercentage = calculateDiscountPercentage(req.getMrpPrice(), req.getSellingPrice());

        Category category1 = categoryRepository.findByCategoryId(req.getCategory());
        if (category1 == null) {
            Category category = new Category();
            category.setCategoryId(req.getCategory());
            category.setLevel(1);
            category.setName(req.getCategory().replace("_", " "));
            category1 = categoryRepository.save(category);
        }

        Category category2 = categoryRepository.findByCategoryId(req.getCategory2());
        if (category2 == null) {
            Category category = new Category();
            category.setCategoryId(req.getCategory2());
            category.setLevel(2);
            category.setParentCategory(category1);
            category.setName(req.getCategory2().replace("_", " "));
            category2 = categoryRepository.save(category);
        }

        Category category3 = categoryRepository.findByCategoryId(req.getCategory3());
        if (category3 == null) {
            Category category = new Category();
            category.setCategoryId(req.getCategory3());
            category.setLevel(3);
            category.setParentCategory(category2);
            category.setName(req.getCategory3().replace("_", " "));
            category3 = categoryRepository.save(category);
        }

        Product product = new Product();
        product.setUser(user); // Set the user (admin) as the owner
        product.setCategory(category3);
        product.setTitle(req.getTitle());
        product.setColor(req.getColor());
        product.setDescription(req.getDescription());
        product.setDiscountPercent(discountPercentage);
        product.setSellingPrice(req.getSellingPrice());
        product.setImages(req.getImages());
        product.setMrpPrice(req.getMrpPrice());
        product.setSizes(req.getSizes());
        product.setCreatedAt(LocalDateTime.now());
        product.setQuantity(100); // Default quantity, can be adjusted

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
        images.add(imageKey);
        product.setImages(images);
        productRepository.save(product);
    }


    @Override
    public void deleteProduct(Long productId) throws ProductException {
        Product product = findProductById(productId);
        productRepository.delete(product);

    }

    @Override
    public Product updateProduct(Long productId, Product product) throws ProductException {
        productRepository.findById(productId);
        product.setId(productId);
        return productRepository.save(product);

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
}
