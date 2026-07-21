package com.akram.cinebase.service.impl;

import com.akram.cinebase.dto.request.CreateReviewRequest;
import com.akram.cinebase.dto.request.UpdateReviewRequest;
import com.akram.cinebase.dto.response.ReviewResponse;
import com.akram.cinebase.entity.Movie;
import com.akram.cinebase.entity.Review;
import com.akram.cinebase.entity.Role;
import com.akram.cinebase.entity.User;
import com.akram.cinebase.enums.RoleName;
import com.akram.cinebase.exception.MovieNotFoundException;
import com.akram.cinebase.exception.ReviewAlreadyExistsException;
import com.akram.cinebase.exception.ReviewNotFoundException;
import com.akram.cinebase.repository.MovieRepository;
import com.akram.cinebase.repository.ReviewRepository;
import com.akram.cinebase.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User user;
    private User anotherUser;
    private Movie movie;
    private Review review;

    @BeforeEach
    void setUp() {

        Role role = Role.builder()
                .id(1L)
                .name(RoleName.ROLE_USER)
                .build();

        user = User.builder()
                .id(1L)
                .username("Akram")
                .email("akram@gmail.com")
                .role(role)
                .build();

        anotherUser = User.builder()
                .id(2L)
                .username("John")
                .email("john@gmail.com")
                .role(role)
                .build();

        movie = Movie.builder()
                .id(1L)
                .title("Inception")
                .averageRating(0.0)
                .totalRatings(0)
                .build();

        review = Review.builder()
                .id(1L)
                .rating(9)
                .reviewText("Excellent Movie")
                .movie(movie)
                .user(user)
                .build();
    }

    private void mockCurrentUser(User currentUser) {

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName())
                .thenReturn(currentUser.getEmail());

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(currentUser.getEmail()))
                .thenReturn(Optional.of(currentUser));
    }

    private CreateReviewRequest createRequest() {

        CreateReviewRequest request = new CreateReviewRequest();
        request.setRating(9);
        request.setReviewText("Excellent Movie");

        return request;
    }

    private UpdateReviewRequest updateRequest() {

        UpdateReviewRequest request = new UpdateReviewRequest();
        request.setRating(8);
        request.setReviewText("Updated Review");

        return request;
    }
    @Test
    void shouldCreateReviewSuccessfully() {

        mockCurrentUser(user);

        CreateReviewRequest request = createRequest();

        when(movieRepository.findById(1L))
                .thenReturn(Optional.of(movie));

        when(reviewRepository.existsByUserIdAndMovieId(user.getId(), movie.getId()))
                .thenReturn(false);

        when(reviewRepository.save(any(Review.class)))
                .thenReturn(review);

        when(reviewRepository.findByMovieId(movie.getId()))
                .thenReturn(List.of(review));

        ReviewResponse response = reviewService.createReview(1L, request);

        assertNotNull(response);
        assertEquals(9, response.getRating());
        assertEquals("Excellent Movie", response.getReviewText());
        assertEquals(movie.getId(), response.getMovieId());
        assertEquals(user.getId(), response.getUserId());

        verify(reviewRepository).save(any(Review.class));
        verify(movieRepository).save(movie);
    }

    @Test
    void shouldThrowReviewAlreadyExistsException() {

        mockCurrentUser(user);

        CreateReviewRequest request = createRequest();

        when(movieRepository.findById(1L))
                .thenReturn(Optional.of(movie));

        when(reviewRepository.existsByUserIdAndMovieId(user.getId(), movie.getId()))
                .thenReturn(true);

        assertThrows(
                ReviewAlreadyExistsException.class,
                () -> reviewService.createReview(1L, request)
        );

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldThrowMovieNotFoundExceptionWhileCreatingReview() {

        mockCurrentUser(user);

        when(movieRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                MovieNotFoundException.class,
                () -> reviewService.createReview(1L, createRequest())
        );

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldReturnReviewById() {

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        ReviewResponse response = reviewService.getReviewById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(9, response.getRating());
        assertEquals("Excellent Movie", response.getReviewText());
        assertEquals("Akram", response.getUsername());
    }

    @Test
    void shouldThrowReviewNotFoundException() {

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ReviewNotFoundException.class,
                () -> reviewService.getReviewById(1L)
        );
    }

    @Test
    void shouldReturnReviewsByMovie() {

        when(movieRepository.existsById(movie.getId()))
                .thenReturn(true);

        when(reviewRepository.findByMovieId(eq(movie.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review)));

        var page = reviewService.getReviewsByMovie(movie.getId(), Pageable.unpaged());

        assertEquals(1, page.getTotalElements());
        assertEquals("Excellent Movie",
                page.getContent().get(0).getReviewText());
    }
    @Test
    void shouldUpdateOwnReviewSuccessfully() {

        mockCurrentUser(user);

        UpdateReviewRequest request = updateRequest();

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        when(reviewRepository.save(any(Review.class)))
                .thenReturn(review);

        when(reviewRepository.findByMovieId(movie.getId()))
                .thenReturn(List.of(review));

        ReviewResponse response = reviewService.updateReview(1L, request);

        assertNotNull(response);
        assertEquals(8, review.getRating());
        assertEquals("Updated Review", review.getReviewText());

        verify(reviewRepository).save(review);
        verify(movieRepository).save(movie);
    }

    @Test
    void shouldThrowAccessDeniedWhenUpdatingOthersReview() {

        mockCurrentUser(user);

        review.setUser(anotherUser);

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        assertThrows(
                AccessDeniedException.class,
                () -> reviewService.updateReview(1L, updateRequest())
        );

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldDeleteReviewSuccessfully() {

        mockCurrentUser(user);

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        when(reviewRepository.findByMovieId(movie.getId()))
                .thenReturn(List.of());

        reviewService.deleteReview(1L);

        verify(reviewRepository).delete(review);
        verify(movieRepository).save(movie);
    }

    @Test
    void shouldThrowAccessDeniedWhenDeletingOthersReview() {

        mockCurrentUser(user);

        review.setUser(anotherUser);

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        assertThrows(
                AccessDeniedException.class,
                () -> reviewService.deleteReview(1L)
        );

        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void shouldReturnCurrentUsersReviews() {

        mockCurrentUser(user);

        when(reviewRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review)));

        var page = reviewService.getMyReviews(Pageable.unpaged());

        assertEquals(1, page.getTotalElements());
        assertEquals("Excellent Movie",
                page.getContent().get(0).getReviewText());
    }

    @Test
    void shouldThrowReviewNotFoundWhileDeleting() {

        mockCurrentUser(user);

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ReviewNotFoundException.class,
                () -> reviewService.deleteReview(1L)
        );

        verify(reviewRepository, never()).delete(any());
    }
}