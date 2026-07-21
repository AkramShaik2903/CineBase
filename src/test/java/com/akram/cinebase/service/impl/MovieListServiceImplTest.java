package com.akram.cinebase.service.impl;

import com.akram.cinebase.dto.request.UpdateMovieListRequest;
import com.akram.cinebase.dto.response.MovieListResponse;
import com.akram.cinebase.entity.Movie;
import com.akram.cinebase.entity.MovieList;
import com.akram.cinebase.entity.Role;
import com.akram.cinebase.entity.User;
import com.akram.cinebase.enums.RoleName;
import com.akram.cinebase.enums.WatchStatus;
import com.akram.cinebase.exception.MovieListNotFoundException;
import com.akram.cinebase.exception.MovieNotFoundException;
import com.akram.cinebase.repository.MovieListRepository;
import com.akram.cinebase.repository.MovieRepository;
import com.akram.cinebase.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieListServiceImplTest {

    @Mock
    private MovieListRepository movieListRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MovieListServiceImpl movieListService;

    private User user;
    private Movie movie;
    private MovieList movieList;

    @BeforeEach
    void setUp() {

        Role role = Role.builder()
                .id(1L)
                .name(RoleName.ROLE_USER)
                .build();

        user = User.builder()
                .id(1L)
                .username("Akram")
                .email("akram@gmail.com")
                .role(role)
                .build();

        movie = Movie.builder()
                .id(1L)
                .title("Inception")
                .posterUrl("poster.jpg")
                .build();

        movieList = new MovieList();
        movieList.setId(1L);
        movieList.setUser(user);
        movieList.setMovie(movie);
        movieList.setWatchStatus(WatchStatus.WATCHLIST);
        movieList.setFavorite(true);
    }

    private void mockCurrentUser() {

        Authentication authentication = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(authentication.getName())
                .thenReturn(user.getEmail());

        when(context.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
    }

    @Test
    void shouldCreateMovieListSuccessfully() {

        mockCurrentUser();

        UpdateMovieListRequest request = new UpdateMovieListRequest();
        request.setWatchStatus(WatchStatus.WATCHLIST);
        request.setFavorite(true);

        when(movieRepository.findById(1L))
                .thenReturn(Optional.of(movie));

        when(movieListRepository.findByUserIdAndMovieId(user.getId(), 1L))
                .thenReturn(Optional.empty());

        movieListService.updateMovieList(1L, request);

        verify(movieListRepository).save(any(MovieList.class));
    }

    @Test
    void shouldThrowMovieNotFoundException() {

        mockCurrentUser();

        when(movieRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                MovieNotFoundException.class,
                () -> movieListService.updateMovieList(1L, new UpdateMovieListRequest())
        );
    }

    @Test
    void shouldReturnMovieListItem() {

        mockCurrentUser();

        when(movieListRepository.findByUserIdAndMovieId(user.getId(), 1L))
                .thenReturn(Optional.of(movieList));

        MovieListResponse response = movieListService.getMovie(1L);

        assertNotNull(response);
        assertEquals(movie.getId(), response.getMovieId());
        assertEquals("Inception", response.getMovieTitle());
        assertTrue(response.getFavorite());
    }

    @Test
    void shouldThrowMovieListNotFoundException() {

        mockCurrentUser();

        when(movieListRepository.findByUserIdAndMovieId(user.getId(), 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                MovieListNotFoundException.class,
                () -> movieListService.getMovie(1L)
        );
    }
    @Test
    void shouldReturnWatchlist() {

        mockCurrentUser();

        when(movieListRepository.findByUserIdAndWatchStatus(
                eq(user.getId()),
                eq(WatchStatus.WATCHLIST),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(movieList)));

        var page = movieListService.getMyWatchlist(Pageable.unpaged());

        assertEquals(1, page.getTotalElements());
        assertEquals(WatchStatus.WATCHLIST,
                page.getContent().get(0).getWatchStatus());
    }

    @Test
    void shouldReturnWatchHistory() {

        mockCurrentUser();

        movieList.setWatchStatus(WatchStatus.WATCHED);

        when(movieListRepository.findByUserIdAndWatchStatus(
                eq(user.getId()),
                eq(WatchStatus.WATCHED),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(movieList)));

        var page = movieListService.getMyWatchHistory(Pageable.unpaged());

        assertEquals(1, page.getTotalElements());
        assertEquals(WatchStatus.WATCHED,
                page.getContent().get(0).getWatchStatus());
    }

    @Test
    void shouldReturnFavorites() {

        mockCurrentUser();

        when(movieListRepository.findByUserIdAndFavoriteTrue(
                eq(user.getId()),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(movieList)));

        var page = movieListService.getMyFavorites(Pageable.unpaged());

        assertEquals(1, page.getTotalElements());
        assertTrue(page.getContent().get(0).getFavorite());
    }

    @Test
    void shouldDeleteMovieListWhenNothingSelected() {

        mockCurrentUser();

        UpdateMovieListRequest request = new UpdateMovieListRequest();
        request.setWatchStatus(WatchStatus.NONE);
        request.setFavorite(false);

        when(movieRepository.findById(1L))
                .thenReturn(Optional.of(movie));

        when(movieListRepository.findByUserIdAndMovieId(user.getId(), 1L))
                .thenReturn(Optional.of(movieList));

        movieListService.updateMovieList(1L, request);

        verify(movieListRepository).delete(movieList);
        verify(movieListRepository, never()).save(any(MovieList.class));
    }
}