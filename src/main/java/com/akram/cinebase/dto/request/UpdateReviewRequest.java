package com.akram.cinebase.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateReviewRequest {

    @Min(value = 1, message = "Rating must be at least 1.")
    @Max(value = 10, message = "Rating cannot be greater than 10.")
    private Integer rating;

    @Size(max = 5000, message = "Review cannot exceed 5000 characters.")
    private String reviewText;
}