package com.akram.cinebase.service;

import com.akram.cinebase.dto.request.CreateMovieRequest;
import com.akram.cinebase.dto.response.MovieResponse;
import com.akram.cinebase.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieService {

    // Create
    MovieResponse createMovie(CreateMovieRequest request);

    // Read
    MovieResponse getMovieById(Long id);

    Page<MovieResponse> getAllMovies(Pageable pageable);

    Page<MovieResponse> searchMovies(String title, Pageable pageable);

    Page<MovieResponse> getMoviesByGenre(Genre genre, Pageable pageable);

    // Update
    MovieResponse updateMovie(Long id, CreateMovieRequest request);

    // Delete
    void deleteMovie(Long id);
}