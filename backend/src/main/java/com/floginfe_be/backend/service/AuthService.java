package com.floginfe_be.backend.service;

import com.floginfe_be.backend.dto.request.LoginRequest;
import com.floginfe_be.backend.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse authenticate(LoginRequest request);

    String validateLogin(String username, String password);
}
