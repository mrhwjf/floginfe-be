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
            return "Ten dang nhap khong duoc de trong";
        }
        if (username.length() < 3 || username.length() > 50) {
            return "Ten dang nhap phai tu 3 den 50 ky tu";
        }
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            return "Ten dang nhap chi co the chua chu cai va so";
        }

        // Validate password
        if (password == null || password.trim().isEmpty()) {
            return "Mat khau khong duoc de trong";
        }
        if (password.length() < 6 || password.length() > 100) {
            return "Mat khau phai tu 6 den 100 ky tu";
        }
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d).+$")) {
            return "Mat khau phai chua it nhat mot chu cai va mot so";
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
