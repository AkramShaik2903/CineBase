package com.akram.cinebase.dto.response;

import com.akram.cinebase.enums.WatchStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MovieListResponse {

    private Long id;

    private Long movieId;
    private String movieTitle;
    private String posterUrl;

    private WatchStatus watchStatus;
    private Boolean favorite;

    private LocalDateTime watchedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}