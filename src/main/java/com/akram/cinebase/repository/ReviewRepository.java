package com.akram.cinebase.repository;

import com.akram.cinebase.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    Page<Review> findByMovieId(Long movieId, Pageable pageable);

    Page<Review> findByUserId(Long userId, Pageable pageable);

    List<Review> findByMovieId(Long movieId);
}