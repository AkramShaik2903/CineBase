package com.akram.cinebase.service;

import com.akram.cinebase.dto.request.LoginRequest;
import com.akram.cinebase.dto.request.SignUpRequest;
import com.akram.cinebase.dto.response.LoginResponse;
import com.akram.cinebase.dto.response.SignUpResponse;

public interface AuthService {
    SignUpResponse signUp(SignUpRequest request);

    LoginResponse login(LoginRequest request);
}
