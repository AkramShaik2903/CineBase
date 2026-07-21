package com.akram.cinebase.dto.request;

import com.akram.cinebase.enums.Genre;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateMovieRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 3000)
    private String description;

    @NotNull(message = "Release year is required")
    @Min(value = 1888, message = "Invalid release year")
    @Max(value = 2100, message = "Invalid release year")
    private Integer releaseYear;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be greater than 0")
    private Integer duration;

    @NotBlank(message = "Language is required")
    private String language;

    @NotBlank(message = "Country is required")
    private String country;

    @NotNull(message = "Genre is required")
    private Genre genre;

    private String posterUrl;

    private String backdropUrl;

    @Pattern(
            regexp = "^(https?://.*)?$",
            message = "Invalid trailer URL"
    )
    private String trailerUrl;
}