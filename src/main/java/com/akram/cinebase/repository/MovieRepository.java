package com.akram.cinebase.repository;

import com.akram.cinebase.entity.Movie;
import com.akram.cinebase.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Check duplicate movie
    boolean existsByTitleAndReleaseYear(String title, Integer releaseYear);

    // Search movies by title
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Filter by genre
    Page<Movie> findByGenre(Genre genre, Pageable pageable);
}