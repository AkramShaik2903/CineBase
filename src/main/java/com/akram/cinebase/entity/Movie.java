package com.akram.cinebase.entity;

import com.akram.cinebase.enums.Genre;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "movies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false,length = 3000)
    private String description;

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String country;

    @Column(length = 1000)
    private String posterUrl;

    @Column(length = 1000)
    private String backdropUrl;

    @Column(length = 1000)
    private String trailerUrl;

    @Builder.Default
    @Column(nullable = false)
    private Double averageRating = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalRatings = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
