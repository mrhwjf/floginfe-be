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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Test cho AuthController
 * 
 * Yêu cầu:
 * a) Test POST /api/auth/login endpoint (3 điểm)
 * b) Test response structure và status codes (1 điểm)
 * c) Test CORS và headers (1 điểm)
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Login API Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthService authService;

    // a) Test POST /api/auth/login endpoint
    @Test
    @DisplayName("POST /api/auth/login - Thanh cong")
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(true);
        mockResponse.setMessage("Dang nhap thanh cong");
        mockResponse.setToken("token123");

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Dang nhap thanh cong"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").value("token123"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Sai mat khau")
    void testLoginInvalidPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("WrongPass123");

        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(false);
        mockResponse.setMessage("Invalid username or password");
        mockResponse.setToken(null);

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("POST /api/auth/login - User khong ton tai")
    void testLoginNonExistentUser() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(false);
        mockResponse.setMessage("Invalid username or password");
        mockResponse.setToken(null);

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Username rong")
    void testLoginEmptyUsername() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("Test123");

        when(authService.validateLogin("", "Test123"))
                .thenReturn("Ten dang nhap khong duoc de trong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Ten dang nhap khong duoc de trong"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Password rong")
    void testLoginEmptyPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("");

        when(authService.validateLogin("testuser", ""))
                .thenReturn("Mat khau khong duoc de trong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Mat khau khong duoc de trong"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Username qua ngan")
    void testLoginShortUsername() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("ab");
        request.setPassword("Test123");

        when(authService.validateLogin("ab", "Test123"))
                .thenReturn("Ten dang nhap phai tu 3 den 50 ky tu");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Ten dang nhap phai tu 3 den 50 ky tu"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Password qua ngan")
    void testLoginShortPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("12345");

        when(authService.validateLogin("testuser", "12345"))
                .thenReturn("Mat khau phai tu 6 den 100 ky tu");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Mat khau phai tu 6 den 100 ky tu"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Username chua ky tu dac biet")
    void testLoginSpecialCharactersInUsername() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("user@123");
        request.setPassword("Test123");

        when(authService.validateLogin("user@123", "Test123"))
                .thenReturn("Ten dang nhap chi co the chua chu cai va so");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Ten dang nhap chi co the chua chu cai va so"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Password khong du phuc tap")
    void testLoginWeakPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("abcdefgh");

        when(authService.validateLogin("testuser", "abcdefgh"))
                .thenReturn("Mat khau phai chua it nhat mot chu cai va mot so");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Mat khau phai chua it nhat mot chu cai va mot so"));
    }


    // b) Test response structure và status codes
    @Test
    @DisplayName("Response structure - Kiem tra cau truc JSON")
    void testResponseStructure() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(true);
        mockResponse.setMessage("Dang nhap thanh cong");
        mockResponse.setToken("token123");

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("Response Content-Type - Kiem tra JSON format")
    void testResponseContentType() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(true);
        mockResponse.setMessage("Dang nhap thanh cong");
        mockResponse.setToken("token123");

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Status codes - Kiem tra 200, 400, 401")
    void testStatusCodes() throws Exception {

        // Success case - 200 OK
        LoginRequest validRequest = new LoginRequest();
        validRequest.setUsername("testuser");
        validRequest.setPassword("Test123");

        LoginResponse successResponse = new LoginResponse();
        successResponse.setSuccess(true);
        successResponse.setMessage("Dang nhap thanh cong");
        successResponse.setToken("token123");

        when(authService.validateLogin("testuser", "Test123"))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(successResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());



        // Validation error - 400 Bad Request
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername("");
        invalidRequest.setPassword("Test123");

        when(authService.validateLogin("", "Test123"))
                .thenReturn("Ten dang nhap khong duoc de trong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());



        // Authentication failed - 401 Unauthorized
        LoginRequest wrongPasswordRequest = new LoginRequest();
        wrongPasswordRequest.setUsername("testuser");
        wrongPasswordRequest.setPassword("WrongPass123");

        LoginResponse failResponse = new LoginResponse();
        failResponse.setSuccess(false);
        failResponse.setMessage("Invalid username or password");

        when(authService.validateLogin("testuser", "WrongPass123"))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(failResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isUnauthorized());
    }


    // c) Test CORS và headers (1 điểm)
    @Test
    @DisplayName("CORS headers - Kiem tra Access-Control-Allow-Origin")
    void testCorsHeaders() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(true);
        mockResponse.setMessage("Dang nhap thanh cong");
        mockResponse.setToken("token123");

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    @DisplayName("CORS preflight - Kiem tra OPTIONS method")
    void testCorsPreflightRequest() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }

    @Test
    @DisplayName("Request headers - Kiem tra Accept header")
    void testRequestHeaders() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(true);
        mockResponse.setMessage("Dang nhap thanh cong");
        mockResponse.setToken("token123");

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Custom headers - Kiem tra khong bi block")
    void testCustomHeaders() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(true);
        mockResponse.setMessage("Dang nhap thanh cong");
        mockResponse.setToken("token123");

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-ID", "test-123")
                        .header("X-Client-Version", "1.0.0")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

}
