package com.akram.cinebase.controller;

import com.akram.cinebase.dto.request.CreateReviewRequest;
import com.akram.cinebase.dto.request.UpdateReviewRequest;
import com.akram.cinebase.dto.response.ReviewResponse;
import com.akram.cinebase.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Reviews",
        description = "APIs for creating, viewing, updating, and deleting movie reviews."
)
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Create Review
    @PostMapping("/movies/{movieId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long movieId,
            @Valid @RequestBody CreateReviewRequest request) {

        ReviewResponse response = reviewService.createReview(movieId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get Review By Id
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(
            @PathVariable Long reviewId) {

        ReviewResponse response = reviewService.getReviewById(reviewId);

        return ResponseEntity.ok(response);
    }

    // Get Reviews Of A Movie
    @GetMapping("/movies/{movieId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByMovie(
            @PathVariable Long movieId,
            @PageableDefault(size = 10, sort = "createdAt")
            Pageable pageable) {

        Page<ReviewResponse> response =
                reviewService.getReviewsByMovie(movieId, pageable);

        return ResponseEntity.ok(response);
    }

    // Get Logged-in User's Reviews
    @GetMapping("/users/me/reviews")
    public ResponseEntity<Page<ReviewResponse>> getMyReviews(
            @PageableDefault(size = 10, sort = "createdAt")
            Pageable pageable) {

        Page<ReviewResponse> response =
                reviewService.getMyReviews(pageable);

        return ResponseEntity.ok(response);
    }

    // Update Review
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {

        ReviewResponse response =
                reviewService.updateReview(reviewId, request);

        return ResponseEntity.ok(response);
    }

    // Delete Review
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId) {

        reviewService.deleteReview(reviewId);

        return ResponseEntity.noContent().build();
    }
}