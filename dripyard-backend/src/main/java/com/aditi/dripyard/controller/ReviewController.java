package com.aditi.dripyard.controller;

import com.aditi.dripyard.exception.ProductException;
import com.aditi.dripyard.exception.ReviewNotFoundException;
import com.aditi.dripyard.exception.UserException;
import com.aditi.dripyard.model.Product;
import com.aditi.dripyard.model.Review;
import com.aditi.dripyard.model.User;
import com.aditi.dripyard.request.CreateReviewRequest;
import com.aditi.dripyard.response.ApiResponse;
import com.aditi.dripyard.service.ProductService;
import com.aditi.dripyard.service.ReviewService;
import com.aditi.dripyard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<Review>> getReviewsByProductId(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<Review> writeReview(
            @RequestBody CreateReviewRequest req,
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        try {
            User user = userService.findUserProfileByJwt(jwt);
            Product product = productService.findProductById(productId);
            Review review = reviewService.createReview(req, user, product);
            return new ResponseEntity<>(review, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ProductException("Failed to create review: " + e.getMessage());
        }
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @RequestBody CreateReviewRequest req,
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String jwt)
            throws UserException, ReviewNotFoundException {
        try {
            User user = userService.findUserProfileByJwt(jwt);
            Review review = reviewService.updateReview(
                    reviewId,
                    req.getReviewText(),
                    req.getReviewRating(),
                    user.getId()
            );
            return ResponseEntity.ok(review);
        } catch (AccessDeniedException e) {
            throw new UserException("You are not authorized to update this review");
        }
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String jwt)
            throws UserException, ReviewNotFoundException {
        try {
            User user = userService.findUserProfileByJwt(jwt);
            reviewService.deleteReview(reviewId, user.getId());

            ApiResponse res = new ApiResponse();
            res.setMessage("Review deleted successfully");
            res.setStatus(true);

            return ResponseEntity.ok(res);
        } catch (AccessDeniedException e) {
            throw new UserException("You are not authorized to delete this review");
        }
    }
}
