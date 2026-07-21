package com.akram.cinebase.security;

import com.akram.cinebase.entity.User;
import com.akram.cinebase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
        private final UserRepository userRepository;

        @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
            System.out.println("CustomUserDetailsService called");
            User user=userRepository.findByEmail(email)
                    .orElseThrow(()->new UsernameNotFoundException("User not found"));

            System.out.println(user.getEmail());
            System.out.println(user.getPassword());
            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities(user.getRole().getName().name())
                    .build();
        }
}
