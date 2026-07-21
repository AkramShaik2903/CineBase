package com.akram.cinebase.service.impl;

import com.akram.cinebase.dto.request.UpdateMovieListRequest;
import com.akram.cinebase.dto.response.MovieListResponse;
import com.akram.cinebase.entity.Movie;
import com.akram.cinebase.entity.MovieList;
import com.akram.cinebase.entity.User;
import com.akram.cinebase.enums.WatchStatus;
import com.akram.cinebase.exception.MovieListNotFoundException;
import com.akram.cinebase.exception.MovieNotFoundException;
import com.akram.cinebase.repository.MovieListRepository;
import com.akram.cinebase.repository.MovieRepository;
import com.akram.cinebase.repository.UserRepository;
import com.akram.cinebase.service.MovieListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovieListServiceImpl implements MovieListService {

    private final MovieListRepository movieListRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Override
    public void updateMovieList(Long movieId, UpdateMovieListRequest request) {

        User currentUser = getCurrentUser();

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() ->
                        new MovieNotFoundException(
                                "Movie not found with id: " + movieId));

        MovieList movieList = movieListRepository
                .findByUserIdAndMovieId(currentUser.getId(), movieId)
                .orElse(new MovieList());

        // First interaction with this movie
        if (movieList.getId() == null) {
            movieList.setUser(currentUser);
            movieList.setMovie(movie);
        }

        // Update watch status
        if (request.getWatchStatus() != null) {

            movieList.setWatchStatus(request.getWatchStatus());

            if (request.getWatchStatus() == WatchStatus.WATCHED) {
                movieList.setWatchedAt(LocalDateTime.now());
            } else {
                movieList.setWatchedAt(null);
            }
        }

        // Update favorite
        if (request.getFavorite()!= null) {
            movieList.setFavorite(request.getFavorite());
        }

        WatchStatus status = movieList.getWatchStatus();

        // Remove relationship if nothing is selected
        if ((status == null || status == WatchStatus.NONE)
                && !movieList.isFavorite()) {

            if (movieList.getId() != null) {
                movieListRepository.delete(movieList);
            }

            return;
        }

        movieListRepository.save(movieList);
    }

    @Override
    public MovieListResponse getMovie(Long movieId) {

        User currentUser = getCurrentUser();

        MovieList movieList = movieListRepository
                .findByUserIdAndMovieId(currentUser.getId(), movieId)
                .orElseThrow(() ->
                        new MovieListNotFoundException(
                                "Movie not found in your list."));

        return mapToResponse(movieList);
    }

    @Override
    public Page<MovieListResponse> getMyWatchlist(Pageable pageable) {

        User currentUser = getCurrentUser();

        return movieListRepository
                .findByUserIdAndWatchStatus(
                        currentUser.getId(),
                        WatchStatus.WATCHLIST,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    public Page<MovieListResponse> getMyWatchHistory(Pageable pageable) {

        User currentUser = getCurrentUser();

        return movieListRepository
                .findByUserIdAndWatchStatus(
                        currentUser.getId(),
                        WatchStatus.WATCHED,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    public Page<MovieListResponse> getMyFavorites(Pageable pageable) {

        User currentUser = getCurrentUser();

        return movieListRepository
                .findByUserIdAndFavoriteTrue(
                        currentUser.getId(),
                        pageable
                )
                .map(this::mapToResponse);
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found."));
    }

    private MovieListResponse mapToResponse(MovieList movieList) {

        MovieListResponse response = new MovieListResponse();

        response.setId(movieList.getId());
        response.setMovieId(movieList.getMovie().getId());
        response.setMovieTitle(movieList.getMovie().getTitle());
        response.setPosterUrl(movieList.getMovie().getPosterUrl());

        response.setWatchStatus(movieList.getWatchStatus());
        response.setFavorite(movieList.isFavorite());

        response.setWatchedAt(movieList.getWatchedAt());
        response.setCreatedAt(movieList.getCreatedAt());
        response.setUpdatedAt(movieList.getUpdatedAt());

        return response;
    }
}