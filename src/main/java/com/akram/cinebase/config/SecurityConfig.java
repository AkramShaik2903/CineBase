package com.akram.cinebase.config;

import com.akram.cinebase.security.CustomUserDetailsService;
import com.akram.cinebase.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth


                        // Public APIs

                        .requestMatchers("/api/auth/**").permitAll()

                        // Swagger
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()


                        // Movie APIs


                        // Admin only
                        .requestMatchers(HttpMethod.POST, "/api/movies").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/movies/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/movies/{id}").hasRole("ADMIN")

                        // Authenticated users
                        .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()


                        // Review APIs


                        .requestMatchers(HttpMethod.POST, "/api/movies/*/reviews").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/movies/*/reviews").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/me/reviews").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").authenticated()


                        // Movie List APIs


                        .requestMatchers(HttpMethod.PUT, "/api/users/me/movies/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/me/movies/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/me/watchlist").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/me/watch-history").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/me/favorites").authenticated()

                        // Everything else
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .formLogin(form -> form.disable())

                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}