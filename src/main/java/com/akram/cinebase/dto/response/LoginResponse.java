package com.akram.cinebase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;

    @Builder.Default
    private String tokenType="Bearer";

    private Long id;

    private String username;

    private String email;

    private String role;
}
