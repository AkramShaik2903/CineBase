package com.akram.cinebase.service.impl;

import com.akram.cinebase.dto.request.CreateMovieRequest;
import com.akram.cinebase.dto.response.MovieResponse;
import com.akram.cinebase.entity.Movie;
import com.akram.cinebase.enums.Genre;
import com.akram.cinebase.exception.MovieAlreadyExistsException;
import com.akram.cinebase.exception.MovieNotFoundException;
import com.akram.cinebase.repository.MovieRepository;
import com.akram.cinebase.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public MovieResponse createMovie(CreateMovieRequest request) {

        if (movieRepository.existsByTitleAndReleaseYear(
                request.getTitle(),
                request.getReleaseYear())) {

            throw new MovieAlreadyExistsException(
                    "Movie with the same title and release year already exists."
            );
        }

        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .releaseYear(request.getReleaseYear())
                .duration(request.getDuration())
                .language(request.getLanguage())
                .country(request.getCountry())
                .genre(request.getGenre())
                .posterUrl(request.getPosterUrl())
                .backdropUrl(request.getBackdropUrl())
                .trailerUrl(request.getTrailerUrl())
                .build();

        Movie savedMovie = movieRepository.save(movie);

        return mapToResponse(savedMovie);
    }

    @Override
    public MovieResponse getMovieById(Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() ->
                        new MovieNotFoundException("Movie not found with id: " + id));

        return mapToResponse(movie);
    }

    @Override
    public Page<MovieResponse> getAllMovies(Pageable pageable) {

        return movieRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<MovieResponse> searchMovies(String title, Pageable pageable) {

        return movieRepository
                .findByTitleContainingIgnoreCase(title, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<MovieResponse> getMoviesByGenre(Genre genre, Pageable pageable) {

        return movieRepository
                .findByGenre(genre, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public MovieResponse updateMovie(Long id, CreateMovieRequest request) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() ->
                        new MovieNotFoundException("Movie not found with id: " + id));

        if (!movie.getTitle().equals(request.getTitle())
                || !movie.getReleaseYear().equals(request.getReleaseYear())) {

            if (movieRepository.existsByTitleAndReleaseYear(
                    request.getTitle(),
                    request.getReleaseYear())) {

                throw new MovieAlreadyExistsException(
                        "Movie with the same title and release year already exists."
                );
            }
        }

        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setReleaseYear(request.getReleaseYear());
        movie.setDuration(request.getDuration());
        movie.setLanguage(request.getLanguage());
        movie.setCountry(request.getCountry());
        movie.setGenre(request.getGenre());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setBackdropUrl(request.getBackdropUrl());
        movie.setTrailerUrl(request.getTrailerUrl());

        Movie updatedMovie = movieRepository.save(movie);

        return mapToResponse(updatedMovie);
    }

    @Override
    public void deleteMovie(Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() ->
                        new MovieNotFoundException("Movie not found with id: " + id));

        movieRepository.delete(movie);
    }

    private MovieResponse mapToResponse(Movie movie) {

        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .releaseYear(movie.getReleaseYear())
                .duration(movie.getDuration())
                .language(movie.getLanguage())
                .country(movie.getCountry())
                .genre(movie.getGenre())
                .posterUrl(movie.getPosterUrl())
                .backdropUrl(movie.getBackdropUrl())
                .trailerUrl(movie.getTrailerUrl())
                .averageRating(movie.getAverageRating())
                .totalRatings(movie.getTotalRatings())
                .createdAt(movie.getCreatedAt())
                .updatedAt(movie.getUpdatedAt())
                .build();
    }
}