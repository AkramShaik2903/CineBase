package com.akram.cinebase.service.impl;

import com.akram.cinebase.dto.request.CreateReviewRequest;
import com.akram.cinebase.dto.request.UpdateReviewRequest;
import com.akram.cinebase.dto.response.ReviewResponse;
import com.akram.cinebase.entity.Movie;
import com.akram.cinebase.entity.Review;
import com.akram.cinebase.entity.User;
import com.akram.cinebase.enums.RoleName;
import com.akram.cinebase.exception.MovieNotFoundException;
import com.akram.cinebase.exception.ReviewAlreadyExistsException;
import com.akram.cinebase.exception.ReviewNotFoundException;
import com.akram.cinebase.repository.MovieRepository;
import com.akram.cinebase.repository.ReviewRepository;
import com.akram.cinebase.repository.UserRepository;
import com.akram.cinebase.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewResponse createReview(Long movieId, CreateReviewRequest request) {

        User currentUser = getCurrentUser();

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() ->
                        new MovieNotFoundException("Movie not found with id : " + movieId));

        if (reviewRepository.existsByUserIdAndMovieId(currentUser.getId(), movieId)) {
            throw new ReviewAlreadyExistsException(
                    "You have already reviewed this movie.");
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .reviewText(request.getReviewText())
                .movie(movie)
                .user(currentUser)
                .build();

        Review savedReview = reviewRepository.save(review);

        recalculateMovieRating(movie);

        return mapToResponse(savedReview);
    }

    @Override
    public ReviewResponse getReviewById(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ReviewNotFoundException("Review not found with id : " + reviewId));

        return mapToResponse(review);
    }

    @Override
    public Page<ReviewResponse> getReviewsByMovie(Long movieId, Pageable pageable) {

        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException(
                    "Movie not found with id : " + movieId);
        }

        return reviewRepository.findByMovieId(movieId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<ReviewResponse> getMyReviews(Pageable pageable) {

        User currentUser = getCurrentUser();

        return reviewRepository.findByUserId(currentUser.getId(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    public ReviewResponse updateReview(Long reviewId,
                                       UpdateReviewRequest request) {

        User currentUser = getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ReviewNotFoundException(
                                "Review not found with id : " + reviewId));

        if (!review.getUser().getId().equals(currentUser.getId()))
         {

            throw new AccessDeniedException(
                    "You are not allowed to update this review.");
        }

        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());

        Review updatedReview = reviewRepository.save(review);

        recalculateMovieRating(updatedReview.getMovie());

        return mapToResponse(updatedReview);
    }

    @Override
    public void deleteReview(Long reviewId) {

        User currentUser = getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ReviewNotFoundException(
                                "Review not found with id : " + reviewId));

        if (!review.getUser().getId().equals(currentUser.getId())
                && !isAdmin(currentUser)) {

            throw new AccessDeniedException(
                    "You are not allowed to delete this review.");
        }

        Movie movie = review.getMovie();

        reviewRepository.delete(review);

        recalculateMovieRating(movie);
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found."));
    }

    private boolean isAdmin(User user) {

        return user.getRole().getName() == RoleName.ROLE_ADMIN;
    }

    private void recalculateMovieRating(Movie movie) {

        var reviews = reviewRepository.findByMovieId(movie.getId());

        if (reviews.isEmpty()) {
            movie.setAverageRating(0.0);
            movie.setTotalRatings(0);
        } else {

            double average = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            movie.setAverageRating(average);
            movie.setTotalRatings(reviews.size());
        }

        movieRepository.save(movie);
    }

    private ReviewResponse mapToResponse(Review review) {

        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .movieId(review.getMovie().getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}