package com.floginfe_be.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.floginfe_be.backend.dto.request.LoginRequest;
import com.floginfe_be.backend.dto.response.LoginResponse;
import com.floginfe_be.backend.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Login API Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

     
    
    // a) Test POST /api/auth/login endpoint (3 điểm)
    @Test
    @DisplayName("POST /api/auth/login - Thanh cong")
    void testLoginSuccess() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin("testuser", "Test123")).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"));

        // Verify
        verify(authService, times(1)).validateLogin("testuser", "Test123");
        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login - That bai voi username khong ton tai")
    void testLoginFailureUserNotFound() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("wronguser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse(false, "Invalid username or password");

        when(authService.validateLogin("wronguser", "Test123")).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));

        // Verify
        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login - That bai voi password sai")
    void testLoginFailureWrongPassword() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("WrongPassword123");

        LoginResponse mockResponse = new LoginResponse(false, "Invalid username or password");

        when(authService.validateLogin("testuser", "WrongPassword123")).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        // Verify
        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login - Validation error: username qua ngan")
    void testLoginValidationErrorUsernameTooShort() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("ab");
        request.setPassword("Test123");

        when(authService.validateLogin("ab", "Test123"))
                .thenReturn("Username must be between 3 and 50 characters");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Username must be between 3 and 50 characters"));

        // Verify authenticate không được gọi khi validation fail
        verify(authService, never()).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login - Validation error: password qua ngan")
    void testLoginValidationErrorPasswordTooShort() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Pass1");

        when(authService.validateLogin("testuser", "Pass1"))
                .thenReturn("Password must be between 6 and 100 characters");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Password must be between 6 and 100 characters"));

        // Verify
        verify(authService, never()).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login - Validation error: username trong")
    void testLoginValidationErrorEmptyUsername() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("Test123");

        when(authService.validateLogin("", "Test123"))
                .thenReturn("Username is required");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Username is required"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Validation error: password khong co chu so")
    void testLoginValidationErrorPasswordNoDigits() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Password");

        when(authService.validateLogin("testuser", "Password"))
                .thenReturn("Password must contain at least one letter and one number");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Password must contain at least one letter and one number"));
    }



    // b) Test response structure và status codes (1 điểm)
    @Test
    @DisplayName("Response Structure: Success response co du cac field")
    void testSuccessResponseStructure() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    @DisplayName("Response Structure: Error response co du cac field")
    void testErrorResponseStructure() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("WrongPass123");

        LoginResponse mockResponse = new LoginResponse(false, "Invalid username or password");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    @DisplayName("Status Code: 200 OK khi login thanh cong")
    void testStatusCode200() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Status Code: 400 Bad Request khi validation fail")
    void testStatusCode400() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("ab");
        request.setPassword("Test123");

        when(authService.validateLogin("ab", "Test123"))
                .thenReturn("Username must be between 3 and 50 characters");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Status Code: 401 Unauthorized khi authentication fail")
    void testStatusCode401() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("WrongPass123");

        LoginResponse mockResponse = new LoginResponse(false, "Invalid username or password");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Status Code: 400 khi request body invalid JSON")
    void testStatusCode400InvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }



    // c) Test CORS và headers (1 điểm)
    @Test
    @DisplayName("CORS: Access-Control-Allow-Origin header ton tai")
    void testCorsHeaderPresent() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Origin", "http://localhost:3000")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("CORS: Allow origin cho localhost:3000")
    void testCorsAllowOrigin() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Origin", "http://localhost:3000")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    @DisplayName("Headers: Content-Type la application/json")
    void testContentTypeHeader() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Headers: Accept application/json request")
    void testAcceptHeader() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CORS: Preflight OPTIONS request")
    void testCorsPreflightRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(options("/api/auth/login")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk());
    }
}
