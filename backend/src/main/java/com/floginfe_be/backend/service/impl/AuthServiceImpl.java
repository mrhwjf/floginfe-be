package com.floginfe_be.backend.service.impl;

import com.floginfe_be.backend.dto.request.LoginRequest;
import com.floginfe_be.backend.dto.response.LoginResponse;
import com.floginfe_be.backend.entity.User;
import com.floginfe_be.backend.repository.UserRepository;
import com.floginfe_be.backend.security.JwtTokenProvider;
import com.floginfe_be.backend.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    // Thông báo final
    public static final String MSG_INVALID_CREDENTIALS = "Invalid username or password";
    public static final String MSG_LOGIN_SUCCESS = "Log in successfully";

    public static final String ERR_USERNAME_EMPTY = "Ten dang nhap khong duoc de trong";
    public static final String ERR_USERNAME_LENGTH = "Ten dang nhap phai tu 3 den 50 ky tu";
    public static final String ERR_USERNAME_INVALID_CHARS = "Ten dang nhap chi co the chua chu cai va so";
    public static final String ERR_PASSWORD_EMPTY = "Mat khau khong duoc de trong";
    public static final String ERR_PASSWORD_LENGTH = "Mat khau phai tu 6 den 100 ky tu";
    public static final String ERR_PASSWORD_COMPLEXITY = "Mat khau phai chua it nhat mot chu cai va mot so";

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        LoginResponse response = new LoginResponse();

        if (userOptional.isEmpty()) {
            response.setSuccess(false);
            response.setMessage(MSG_INVALID_CREDENTIALS);
            return response;
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.setSuccess(false);
            response.setMessage(MSG_INVALID_CREDENTIALS);
            return response;
        }

        String jwt = tokenProvider.generateToken(user.getUsername());

        response.setSuccess(true);
        response.setMessage(MSG_LOGIN_SUCCESS);
        response.setToken(jwt);

        return response;
    }

    @Override
    public String validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return ERR_USERNAME_EMPTY;
        }

        if (username.length() < 3 || username.length() > 50) {
            return ERR_USERNAME_LENGTH;
        }

        if (!username.matches("^[a-zA-Z0-9]+$")) {
            return ERR_USERNAME_INVALID_CHARS;
        }

        if (password == null || password.isEmpty()) {
            return ERR_PASSWORD_EMPTY;
        }

        if (password.length() < 6 || password.length() > 100) {
            return ERR_PASSWORD_LENGTH;
        }

        if (!password.matches(".*[a-zA-Z]+.*") || !password.matches(".*\\d+.*")) {
            return ERR_PASSWORD_COMPLEXITY;
        }

        return "";
    }
}

