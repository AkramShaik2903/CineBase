package com.akram.cinebase.service.impl;

import com.akram.cinebase.dto.request.CreateMovieRequest;
import com.akram.cinebase.dto.response.MovieResponse;
import com.akram.cinebase.entity.Movie;
import com.akram.cinebase.enums.Genre;
import com.akram.cinebase.exception.MovieAlreadyExistsException;
import com.akram.cinebase.exception.MovieNotFoundException;
import com.akram.cinebase.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    private CreateMovieRequest createRequest() {

        CreateMovieRequest request = new CreateMovieRequest();
        request.setTitle("Inception");
        request.setDescription("Sci-Fi Movie");
        request.setReleaseYear(2010);
        request.setDuration(148);
        request.setLanguage("English");
        request.setCountry("USA");
        request.setGenre(Genre.ACTION);
        request.setPosterUrl("poster");
        request.setBackdropUrl("backdrop");
        request.setTrailerUrl("trailer");

        return request;
    }

    private Movie createMovie() {

        return Movie.builder()
                .id(1L)
                .title("Inception")
                .description("Sci-Fi Movie")
                .releaseYear(2010)
                .duration(148)
                .language("English")
                .country("USA")
                .genre(Genre.ACTION)
                .posterUrl("poster")
                .backdropUrl("backdrop")
                .trailerUrl("trailer")
                .averageRating(8.9)
                .totalRatings(100)
                .build();
    }

    @Test
    void shouldCreateMovieSuccessfully() {

        CreateMovieRequest request = createRequest();
        Movie movie = createMovie();

        when(movieRepository.existsByTitleAndReleaseYear(
                request.getTitle(),
                request.getReleaseYear()))
                .thenReturn(false);

        when(movieRepository.save(any(Movie.class)))
                .thenReturn(movie);

        MovieResponse response = movieService.createMovie(request);

        assertNotNull(response);
        assertEquals("Inception", response.getTitle());
        assertEquals(2010, response.getReleaseYear());
        assertEquals(Genre.ACTION, response.getGenre());

        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void shouldThrowMovieAlreadyExistsException() {

        CreateMovieRequest request = createRequest();

        when(movieRepository.existsByTitleAndReleaseYear(
                request.getTitle(),
                request.getReleaseYear()))
                .thenReturn(true);

        assertThrows(
                MovieAlreadyExistsException.class,
                () -> movieService.createMovie(request)
        );

        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void shouldReturnMovieById() {

        Movie movie = createMovie();

        when(movieRepository.findById(1L))
                .thenReturn(Optional.of(movie));

        MovieResponse response = movieService.getMovieById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Inception", response.getTitle());
    }

    @Test
    void shouldThrowMovieNotFoundException() {

        when(movieRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                MovieNotFoundException.class,
                () -> movieService.getMovieById(1L)
        );
    }

    @Test
    void shouldDeleteMovieSuccessfully() {

        Movie movie = createMovie();

        when(movieRepository.findById(1L))
                .thenReturn(Optional.of(movie));

        movieService.deleteMovie(1L);

        verify(movieRepository).delete(movie);
    }

}