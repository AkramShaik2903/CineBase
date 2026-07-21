package com.akram.cinebase.service;

import com.akram.cinebase.dto.request.UpdateMovieListRequest;
import com.akram.cinebase.dto.response.MovieListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieListService {

    void updateMovieList(Long movieId, UpdateMovieListRequest request);

    MovieListResponse getMovie(Long movieId);

    Page<MovieListResponse> getMyWatchlist(Pageable pageable);

    Page<MovieListResponse> getMyWatchHistory(Pageable pageable);

    Page<MovieListResponse> getMyFavorites(Pageable pageable);
}