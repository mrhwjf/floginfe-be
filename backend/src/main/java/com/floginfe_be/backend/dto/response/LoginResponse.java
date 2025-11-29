package com.floginfe_be.backend.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private String token; // JWT
}