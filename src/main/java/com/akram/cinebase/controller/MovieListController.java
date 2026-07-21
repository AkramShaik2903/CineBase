package com.akram.cinebase.controller;

import com.akram.cinebase.dto.request.UpdateMovieListRequest;
import com.akram.cinebase.dto.response.MovieListResponse;
import com.akram.cinebase.service.MovieListService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Movie Lists",
        description = "APIs for updating watchlist, watch-history and favorites."
)
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class MovieListController {

    private final MovieListService movieListService;

    @PutMapping("/movies/{movieId}")
    public ResponseEntity<Void> updateMovieList(
            @PathVariable Long movieId,
            @Valid @RequestBody UpdateMovieListRequest request) {

        movieListService.updateMovieList(movieId, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<MovieListResponse> getMovie(
            @PathVariable Long movieId) {

        return ResponseEntity.ok(
                movieListService.getMovie(movieId)
        );
    }

    @GetMapping("/watchlist")
    public ResponseEntity<Page<MovieListResponse>> getMyWatchlist(
            Pageable pageable) {

        return ResponseEntity.ok(
                movieListService.getMyWatchlist(pageable)
        );
    }

    @GetMapping("/watch-history")
    public ResponseEntity<Page<MovieListResponse>> getMyWatchHistory(
            Pageable pageable) {

        return ResponseEntity.ok(
                movieListService.getMyWatchHistory(pageable)
        );
    }

    @GetMapping("/favorites")
    public ResponseEntity<Page<MovieListResponse>> getMyFavorites(
            Pageable pageable) {

        return ResponseEntity.ok(
                movieListService.getMyFavorites(pageable)
        );
    }
}