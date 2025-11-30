package com.floginfe_be.backend.controller;

import com.floginfe_be.backend.dto.request.LoginRequest;
import com.floginfe_be.backend.dto.response.LoginResponse;
import com.floginfe_be.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Validate input
        String validationError = authService.validateLogin(request.getUsername(), request.getPassword());
        if (!validationError.isEmpty()) {
            LoginResponse errorResponse = new LoginResponse(false, validationError);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Authenticate user
        LoginResponse response = authService.authenticate(request);
        
        if (!response.isSuccess()) {
            return ResponseEntity.status(401).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
}