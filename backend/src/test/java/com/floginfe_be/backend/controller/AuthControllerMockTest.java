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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Backend Mock Tests")
class AuthControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;


   
    // a) Mock AuthService với @MockitoBean 
    @Test
    @DisplayName("Mock: AuthService tra ve response thanh cong")
    void testMockAuthServiceSuccess() throws Exception {
        // Arrange 
        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        // Mock behavior
        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("Mock: AuthService tra ve loi - username khong hop le")
    void testMockAuthServiceValidationError() throws Exception {
        // Arrange 
        when(authService.validateLogin(anyString(), anyString()))
                .thenReturn("Ten dang nhap phai tu 3 den 50 ky tu");

        LoginRequest request = new LoginRequest();
        request.setUsername("ab");
        request.setPassword("Test123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Ten dang nhap phai tu 3 den 50 ky tu"));
    }

    @Test
    @DisplayName("Mock: AuthService tra ve loi - authentication that bai")
    void testMockAuthServiceAuthenticationError() throws Exception {
        // Arrange 
        LoginResponse mockResponse = new LoginResponse(false, "Invalid username or password");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername("wronguser");
        request.setPassword("wrongpass");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

   
    // b) Test controller với mocked service 
    @Test
    @DisplayName("Mock: Controller voi mocked service success")
    void testLoginWithMockedService() throws Exception {
        // Arrange
        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"Test123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("Mock: Controller xu ly nhieu request voi mock khac nhau")
    void testMultipleRequestsWithDifferentMocks() throws Exception {
       
        LoginResponse successResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin("user1", "Pass123")).thenReturn("");
        when(authService.authenticate(argThat(req ->
                req != null &&
                "user1".equals(req.getUsername()) &&
                "Pass123".equals(req.getPassword())
        ))).thenReturn(successResponse);

        LoginRequest request1 = new LoginRequest();
        request1.setUsername("user1");
        request1.setPassword("Pass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Second request - failure
        LoginResponse failResponse = new LoginResponse(false, "Invalid username or password");

        when(authService.validateLogin("user2", "WrongPass")).thenReturn("");
        when(authService.authenticate(argThat(req ->
                req != null &&
                "user2".equals(req.getUsername()) &&
                "WrongPass".equals(req.getPassword())
        ))).thenReturn(failResponse);

        LoginRequest request2 = new LoginRequest();
        request2.setUsername("user2");
        request2.setPassword("WrongPass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Mock: Controller khong goi authenticate khi validation fail")
    void testControllerSkipsAuthenticateOnValidationError() throws Exception {
        // Arrange
        when(authService.validateLogin("", "Pass123"))
                .thenReturn("Ten dang nhap khong duoc de trong");

        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("Pass123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Assert
        verify(authService, never()).authenticate(any(LoginRequest.class));
    }

   
    // c) Verify mock interactions 
    @Test
    @DisplayName("Verify: Service duoc goi dung 1 lan")
    void testVerifyServiceCalledOnce() throws Exception {
        // Arrange
        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"Test123\"}"))
                .andExpect(status().isOk());

        // Assert - Verify
        verify(authService, times(1)).validateLogin(anyString(), anyString());
        verify(authService, times(1)).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Verify: Service duoc goi voi parameters dung")
    void testVerifyServiceCalledWithCorrectParams() throws Exception {
        // Arrange
        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin("testuser", "Test123")).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        // Act
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert
        verify(authService).validateLogin("testuser", "Test123");
        verify(authService).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Verify: Service khong duoc goi khi request invalid")
    void testVerifyServiceNotCalledOnInvalidRequest() throws Exception {
        // Arrange 
        when(authService.validateLogin(any(), any()))
                .thenReturn("Invalid request");

        // Act
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalid\":\"json\"}"))
                .andExpect(status().isBadRequest());

        // Assert 
        verify(authService, never()).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Verify: Service duoc goi nhieu lan cho multiple requests")
    void testVerifyServiceCalledMultipleTimes() throws Exception {
        // Arrange
        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act 
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user1\",\"password\":\"Pass123\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user2\",\"password\":\"Pass456\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user3\",\"password\":\"Pass789\"}"))
                .andExpect(status().isOk());

        // Assert 
        verify(authService, times(3)).validateLogin(anyString(), anyString());
        verify(authService, times(3)).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Verify: Kiem tra thu tu goi methods")
    void testVerifyMethodCallOrder() throws Exception {
        // Arrange
        LoginResponse mockResponse = new LoginResponse(true, "Login successful");

        when(authService.validateLogin(anyString(), anyString())).thenReturn("");
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(mockResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        // Act
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert 
        var inOrder = inOrder(authService);
        inOrder.verify(authService).validateLogin(anyString(), anyString());
        inOrder.verify(authService).authenticate(any(LoginRequest.class));
    }
}
