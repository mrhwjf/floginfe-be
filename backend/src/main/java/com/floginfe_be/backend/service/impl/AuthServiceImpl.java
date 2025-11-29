package com.floginfe_be.backend.service.impl;

import com.floginfe_be.backend.dto.request.LoginRequest;
import com.floginfe_be.backend.dto.response.LoginResponse;
import com.floginfe_be.backend.entity.User;
import com.floginfe_be.backend.repository.UserRepository;
import com.floginfe_be.backend.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String validateLogin(String username, String password) {
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            return "Username is required";
        }
        if (username.length() < 3 || username.length() > 50) {
            return "Username must be between 3 and 50 characters";
        }
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            return "Invalid username format";
        }

        // Validate password
        if (password == null || password.trim().isEmpty()) {
            return "Password is required";
        }
        if (password.length() < 6 || password.length() > 100) {
            return "Password must be between 6 and 100 characters";
        }
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d).+$")) {
            return "Password must contain at least one letter and one number";
        }

        return ""; // No validation errors
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            return new LoginResponse(false, "Invalid username or password");
        }

        User user = userOptional.get();

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new LoginResponse(false, "Invalid username or password");
        }

        return new LoginResponse(true, "Login successful");
    }
}
