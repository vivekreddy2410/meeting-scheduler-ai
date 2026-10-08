package com.meetingscheduler.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthUserRepository authUserRepository,
            PasswordEncoder passwordEncoder) {

        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // REGISTER
    public String register(RegisterRequest request) {

        if (request.getName() == null ||
            request.getEmail() == null ||
            request.getPassword() == null) {

            return "All fields are required";
        }

        if (authUserRepository.existsByEmail(request.getEmail())) {
            return "Email already registered";
        }

        AuthUser user = new AuthUser();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Store encrypted password
        user.setPassword(
            passwordEncoder.encode(request.getPassword())
        );

        user.setRole("USER");

        authUserRepository.save(user);

        return "Registration successful";
    }

    // LOGIN
    public boolean login(String email, String password) {

        if (email == null || password == null) {
            return false;
        }

        AuthUser user = authUserRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return false;
        }

        return passwordEncoder.matches(
                password,
                user.getPassword()
        );
    }
}
