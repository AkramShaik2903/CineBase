package com.akram.cinebase.controller;

import com.akram.cinebase.dto.request.CreateMovieRequest;
import com.akram.cinebase.dto.response.MovieResponse;
import com.akram.cinebase.enums.Genre;
import com.akram.cinebase.service.MovieService;
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
        name = "Movies",
        description = "APIs for browsing, searching, filtering, and retrieving movies."
)
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // Create Movie
    @PostMapping
    public ResponseEntity<MovieResponse> createMovie(
            @Valid @RequestBody CreateMovieRequest request) {

        MovieResponse response = movieService.createMovie(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get Movie By Id
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {

        MovieResponse response = movieService.getMovieById(id);

        return ResponseEntity.ok(response);
    }

    // Get All Movies (Pagination)
    @GetMapping
    public ResponseEntity<Page<MovieResponse>> getAllMovies(
            @PageableDefault(size = 10, sort = "title")
            Pageable pageable) {

        Page<MovieResponse> response = movieService.getAllMovies(pageable);

        return ResponseEntity.ok(response);
    }

    // Search Movies By Title
    @GetMapping("/search")
    public ResponseEntity<Page<MovieResponse>> searchMovies(
            @RequestParam String title,
            @PageableDefault(size = 10, sort = "title")
            Pageable pageable) {

        Page<MovieResponse> response =
                movieService.searchMovies(title, pageable);

        return ResponseEntity.ok(response);
    }

    // Filter Movies By Genre
    @GetMapping("/genre/{genre}")
    public ResponseEntity<Page<MovieResponse>> getMoviesByGenre(
            @PathVariable String genre,
            @PageableDefault(size = 10, sort = "title")
            Pageable pageable) {

        Genre genreEnum;

        try {
            genreEnum = Genre.valueOf(genre.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid genre: " + genre);
        }

        Page<MovieResponse> response =
                movieService.getMoviesByGenre(genreEnum, pageable);

        return ResponseEntity.ok(response);
    }

    // Update Movie
    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody CreateMovieRequest request) {

        MovieResponse response =
                movieService.updateMovie(id, request);

        return ResponseEntity.ok(response);
    }

    // Delete Movie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {

        movieService.deleteMovie(id);

        return ResponseEntity.noContent().build();
    }
}