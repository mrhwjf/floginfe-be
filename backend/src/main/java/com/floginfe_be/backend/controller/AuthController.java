package com.floginfe_be.backend.controller;


import com.floginfe_be.backend.dto.request.LoginRequest;
import com.floginfe_be.backend.dto.response.LoginResponse;
import com.floginfe_be.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Endpoint chung cho Authentication
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/login") // Endpoint POST /api/auth/login
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        // Thực hiện validation đơn giản trước khi gọi service
        String validationError = authService.validateLogin(request.getUsername(), request.getPassword());
        if (!validationError.isEmpty()) {
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(validationError);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Gọi service để xử lý nghiệp vụ Login
        LoginResponse response = authService.authenticate(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // Trả về 401 Unauthorized nếu đăng nhập thất bại
            return ResponseEntity.status(401).body(response);
        }
    }
}