package com.akram.cinebase.service.impl;

import com.akram.cinebase.dto.request.LoginRequest;
import com.akram.cinebase.dto.request.SignUpRequest;
import com.akram.cinebase.dto.response.LoginResponse;
import com.akram.cinebase.dto.response.SignUpResponse;
import com.akram.cinebase.entity.Role;
import com.akram.cinebase.entity.User;
import com.akram.cinebase.enums.RoleName;
import com.akram.cinebase.exception.EmailAlreadyExistsException;
import com.akram.cinebase.repository.RoleRepository;
import com.akram.cinebase.repository.UserRepository;
import com.akram.cinebase.security.CustomUserDetailsService;
import com.akram.cinebase.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldSignUpSuccessfully() {

        SignUpRequest request = new SignUpRequest();
        request.setUsername("Akram");
        request.setEmail("akram@gmail.com");
        request.setPassword("password123");

        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);

        User savedUser = User.builder()
                .id(1L)
                .username("Akram")
                .email("akram@gmail.com")
                .password("encodedPassword")
                .role(role)
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(roleRepository.findByName(RoleName.ROLE_USER))
                .thenReturn(Optional.of(role));

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        SignUpResponse response = authService.signUp(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Akram", response.getUsername());
        assertEquals("akram@gmail.com", response.getEmail());
        assertEquals("ROLE_USER", response.getRole());
        assertEquals("User Registered Successfully", response.getMessage());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(request.getPassword());
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request = new LoginRequest();
        request.setEmail("akram@gmail.com");
        request.setPassword("password123");

        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);

        User user = User.builder()
                .id(1L)
                .username("Akram")
                .email("akram@gmail.com")
                .password("encodedPassword")
                .role(role)
                .build();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .builder()
                        .username("akram@gmail.com")
                        .password("encodedPassword")
                        .authorities("ROLE_USER")
                        .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(customUserDetailsService.loadUserByUsername(request.getEmail()))
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(1L, response.getId());
        assertEquals("Akram", response.getUsername());
        assertEquals("akram@gmail.com", response.getEmail());
        assertEquals("ROLE_USER", response.getRole());

        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void shouldThrowEmailAlreadyExistsException() {

        SignUpRequest request = new SignUpRequest();
        request.setUsername("Akram");
        request.setEmail("akram@gmail.com");
        request.setPassword("password123");

        User existingUser = User.builder()
                .email("akram@gmail.com")
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(existingUser));

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.signUp(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }
}