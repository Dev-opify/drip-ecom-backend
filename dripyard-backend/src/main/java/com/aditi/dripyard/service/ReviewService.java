package com.aditi.dripyard.service;

import com.aditi.dripyard.exception.ReviewNotFoundException;
import com.aditi.dripyard.model.Product;
import com.aditi.dripyard.model.Review;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.request.CreateReviewRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

public interface ReviewService {

    Review createReview(CreateReviewRequest req,
                        User user,
                        Product product);

    List<Review> getReviewsByProductId(Long productId);

    Review updateReview(Long reviewId,
                        String reviewText,
                        double rating,
                        Long userId) throws ReviewNotFoundException, AccessDeniedException;

    void deleteReview(Long reviewId, Long userId) throws ReviewNotFoundException, AccessDeniedException;
}

