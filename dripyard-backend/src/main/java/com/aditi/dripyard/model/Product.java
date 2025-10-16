// dripyard-backend/src/main/java/com/aditi/dripyard/model/Product.java
package com.aditi.dripyard.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String brand;
    private int mrpPrice;
    private int sellingPrice;
    private int discountPercent;
    private int quantity;
    private String color;
    @ElementCollection
    private List<String> images = new ArrayList<>();

    private int numRatings;

    @ManyToOne
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id") // Now linked to the User (Admin)
    private User user;

    private LocalDateTime createdAt;
    private String Sizes;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Transient
    public Double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 4.5; // Default rating if no reviews
        }
        return reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(4.5);
    }

    private boolean in_stock = true;
}