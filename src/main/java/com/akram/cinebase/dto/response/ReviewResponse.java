package com.akram.cinebase.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {

    private Long id;

    private Integer rating;

    private String reviewText;

    private Long movieId;

    private Long userId;

    private String username;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}