package com.akram.cinebase.config;

import com.akram.cinebase.entity.Role;
import com.akram.cinebase.entity.User;
import com.akram.cinebase.enums.RoleName;
import com.akram.cinebase.repository.RoleRepository;
import com.akram.cinebase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Create USER role if it doesn't exist
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(RoleName.ROLE_USER)
                                .build()
                ));

        // Create ADMIN role if it doesn't exist
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(RoleName.ROLE_ADMIN)
                                .build()
                ));

        // Create default admin if missing
        if (userRepository.findByEmail("admin@cinebase.com").isEmpty()) {

            User admin = User.builder()
                    .username("Administrator")
                    .email("admin@cinebase.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(adminRole)
                    .build();

            userRepository.save(admin);
        }

        // Create default user if missing
        if (userRepository.findByEmail("user@cinebase.com").isEmpty()) {

            User user = User.builder()
                    .username("Test User")
                    .email("user@cinebase.com")
                    .password(passwordEncoder.encode("User@123"))
                    .role(userRole)
                    .build();

            userRepository.save(user);
        }

        System.out.println("\n======================================================");
        System.out.println("        CineBase Default Test Accounts");
        System.out.println("======================================================");

        System.out.println("\nADMIN");
        System.out.println("Email    : admin@cinebase.com");
        System.out.println("Password : Admin@123");

        System.out.println("\nUSER");
        System.out.println("Email    : user@cinebase.com");
        System.out.println("Password : User@123");

        System.out.println("\n======================================================");
    }
}