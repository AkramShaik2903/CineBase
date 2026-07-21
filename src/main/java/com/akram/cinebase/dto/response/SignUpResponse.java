package com.akram.cinebase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String message;
}
