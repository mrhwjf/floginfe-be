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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Backend Mocking Test cho AuthController
 * 
 * Yêu cầu:
 * a) Mock AuthService với @MockitoBean (1 điểm)
 * b) Test controller với mocked service (1 điểm)
 * c) Verify mock interactions (0.5 điểm)
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
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
        // Setup mock response
        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(true);
        mockResponse.setMessage("Success");
        mockResponse.setToken("mock-token");

        // Mock behavior
        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("Pass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("mock-token"));
    }

    @Test
    @DisplayName("Mock: AuthService tra ve loi validation")
    void testMockAuthServiceValidationError() throws Exception {
        // Mock validation error
        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("Username khong hop le");

        LoginRequest request = new LoginRequest();
        request.setUsername("ab");
        request.setPassword("Pass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Username khong hop le"));
    }

    @Test
    @DisplayName("Mock: AuthService tra ve loi dang nhap")
    void testMockAuthServiceLoginFailure() throws Exception {
        // Setup mock failure response
        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(false);
        mockResponse.setMessage("Invalid username or password");
        mockResponse.setToken(null);

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername("wronguser");
        request.setPassword("wrongpass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }




    // b) Test controller với mocked service 
    @Test
    @DisplayName("Mock: Controller voi mocked service success")
    void testLoginWithMockedService() throws Exception {
        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(true);
        mockResponse.setMessage("Success");
        mockResponse.setToken("mock-token");

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any()))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test\",\"password\":\"Pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("mock-token"));
    }

    @Test
    @DisplayName("Mock: Controller xu ly nhieu request voi mock khac nhau")
    void testMultipleRequestsWithDifferentMocks() throws Exception {
        // First request - success
        LoginResponse successResponse = new LoginResponse();
        successResponse.setSuccess(true);
        successResponse.setMessage("Success");
        successResponse.setToken("token-1");

        when(authService.validateLogin("user1", "Pass123"))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(successResponse);

        LoginRequest request1 = new LoginRequest();
        request1.setUsername("user1");
        request1.setPassword("Pass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-1"));

        // Second request - failure
        LoginResponse failResponse = new LoginResponse();
        failResponse.setSuccess(false);
        failResponse.setMessage("Invalid credentials");

        when(authService.validateLogin("user2", "WrongPass"))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(failResponse);

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
    @DisplayName("Mock: Controller khong goi service khi validation fail")
    void testControllerSkipsServiceOnValidationError() throws Exception {
        when(authService.validateLogin("", "Pass123"))
                .thenReturn("Ten dang nhap khong duoc de trong");

        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("Pass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verify authenticate không được gọi khi validation fail
        verify(authService, never()).authenticate(any(LoginRequest.class));
    }



    // c) Verify mock interactions 
    @Test
    @DisplayName("Verify: Service được gọi đúng 1 lần")
    void testVerifyServiceCalledOnce() throws Exception {
        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(true);
        mockResponse.setMessage("Success");
        mockResponse.setToken("mock-token");

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any()))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test\",\"password\":\"Pass123\"}"))
                .andExpect(status().isOk());

        verify(authService, times(1)).authenticate(any());
        verify(authService, times(1)).validateLogin(any(String.class), any(String.class));
    }

    @Test
    @DisplayName("Verify: Service được gọi với parameters đúng")
    void testVerifyServiceCalledWithCorrectParams() throws Exception {
        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(true);
        mockResponse.setMessage("Success");
        mockResponse.setToken("mock-token");

        when(authService.validateLogin("testuser", "Pass123"))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Pass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Verify với parameters cụ thể
        verify(authService).validateLogin("testuser", "Pass123");
        verify(authService).authenticate(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Verify: Service không được gọi khi request invalid")
    void testVerifyServiceNotCalledOnInvalidRequest() throws Exception {
        // Mock để xử lý null username/password
        when(authService.validateLogin(any(), any()))
                .thenReturn("Invalid request");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalid\":\"json\"}"))
                .andExpect(status().isBadRequest());

        // Verify authenticate không được gọi
        verify(authService, never()).authenticate(any());
    }

    @Test
    @DisplayName("Verify: Service được gọi nhiều lần cho multiple requests")
    void testVerifyServiceCalledMultipleTimes() throws Exception {
        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(true);
        mockResponse.setMessage("Success");
        mockResponse.setToken("mock-token");

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any()))
                .thenReturn(mockResponse);

        // First request
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user1\",\"password\":\"Pass123\"}"))
                .andExpect(status().isOk());

        // Second request
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user2\",\"password\":\"Pass456\"}"))
                .andExpect(status().isOk());

        // Third request
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user3\",\"password\":\"Pass789\"}"))
                .andExpect(status().isOk());

        // Verify được gọi đúng 3 lần
        verify(authService, times(3)).authenticate(any());
        verify(authService, times(3)).validateLogin(any(String.class), any(String.class));
    }

    @Test
    @DisplayName("Verify: Kiểm tra thứ tự gọi methods")
    void testVerifyMethodCallOrder() throws Exception {
        LoginResponse mockResponse = new LoginResponse();
        mockResponse.setSuccess(true);
        mockResponse.setMessage("Success");
        mockResponse.setToken("mock-token");

        when(authService.validateLogin(any(String.class), any(String.class)))
                .thenReturn("");
        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(mockResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("Pass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Verify thứ tự gọi: validateLogin trước, authenticate sau
        var inOrder = inOrder(authService);
        inOrder.verify(authService).validateLogin(any(String.class), any(String.class));
        inOrder.verify(authService).authenticate(any(LoginRequest.class));
    }
}

