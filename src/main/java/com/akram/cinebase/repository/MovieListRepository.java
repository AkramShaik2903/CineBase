package com.akram.cinebase.repository;

import com.akram.cinebase.entity.MovieList;
import com.akram.cinebase.enums.WatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieListRepository extends JpaRepository<MovieList, Long> {

    Optional<MovieList> findByUserIdAndMovieId(Long userId, Long movieId);

    Page<MovieList> findByUserIdAndWatchStatus(
            Long userId,
            WatchStatus watchStatus,
            Pageable pageable
    );

    Page<MovieList> findByUserIdAndFavoriteTrue(
            Long userId,
            Pageable pageable
    );
}