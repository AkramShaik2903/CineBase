package com.akram.cinebase.dto.request;

import com.akram.cinebase.enums.WatchStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMovieListRequest {

    private WatchStatus watchStatus;

    private Boolean favorite;
}