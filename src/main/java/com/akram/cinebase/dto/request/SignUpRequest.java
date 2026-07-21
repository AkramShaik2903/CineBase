package com.akram.cinebase.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {
    @NotBlank(message = "Username is Required")
    @Size(min = 3,max = 30,message = "Username must be between 3 and 30 characters")
    private String username;

    @NotBlank(message = "Email is Required")
    @Email(message = "Please Enter a valid email address")
    private String email;

    @NotBlank(message = "Password is Required")
    private String password;
}
