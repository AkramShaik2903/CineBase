package com.akram.cinebase.dto.response;

import com.akram.cinebase.enums.Genre;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MovieResponse {

    private Long id;

    private String title;

    private String description;

    private Integer releaseYear;

    private Integer duration;

    private String language;

    private String country;

    private Genre genre;

    private String posterUrl;

    private String backdropUrl;

    private String trailerUrl;

    private Double averageRating;

    private Integer totalRatings;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}