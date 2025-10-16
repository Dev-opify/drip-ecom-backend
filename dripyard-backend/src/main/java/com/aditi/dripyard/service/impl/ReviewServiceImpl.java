package com.aditi.dripyard.service.impl;

import com.aditi.dripyard.exception.ReviewNotFoundException;
import com.aditi.dripyard.model.Product;
import com.aditi.dripyard.model.Review;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.repository.ReviewRepository;
import com.aditi.dripyard.request.CreateReviewRequest;
import com.aditi.dripyard.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public Review createReview(CreateReviewRequest req,
                               User user,
                               Product product) {
        Review newReview = new Review();
        newReview.setReviewText(req.getReviewText());
        newReview.setRating(req.getReviewRating());
        newReview.setProductImages(req.getProductImages());
        newReview.setUser(user);
        newReview.setProduct(product);

        product.getReviews().add(newReview);

        return reviewRepository.save(newReview);
    }

    @Override
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findReviewsByProductId(productId);
    }

    @Override
    public Review updateReview(Long reviewId,
                               String reviewText,
                               double rating,
                               Long userId) throws ReviewNotFoundException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review Not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to update this review");
        }

        review.setReviewText(reviewText);
        review.setRating(rating);
        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) throws ReviewNotFoundException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review Not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this review");
        }

        reviewRepository.delete(review);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public void deleteReviewByAdmin(Long reviewId) throws ReviewNotFoundException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review Not found"));
        reviewRepository.delete(review);
    }
}
