package com.akram.cinebase.service;

import com.akram.cinebase.dto.request.CreateReviewRequest;
import com.akram.cinebase.dto.request.UpdateReviewRequest;
import com.akram.cinebase.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse createReview(Long movieId, CreateReviewRequest request);

    ReviewResponse getReviewById(Long reviewId);

    Page<ReviewResponse> getReviewsByMovie(Long movieId, Pageable pageable);

    Page<ReviewResponse> getMyReviews(Pageable pageable);

    ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request);

    void deleteReview(Long reviewId);
}