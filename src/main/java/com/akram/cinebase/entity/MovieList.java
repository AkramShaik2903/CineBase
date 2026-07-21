package com.akram.cinebase.entity;

import com.akram.cinebase.enums.WatchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "movie_lists",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "movie_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MovieList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WatchStatus watchStatus = WatchStatus.NONE;

    @Column(nullable = false)
    private boolean favorite = false;

    private LocalDateTime watchedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}