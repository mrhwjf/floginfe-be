package com.floginfe_be.backend;

import com.floginfe_be.backend.dto.request.LoginRequest;
import com.floginfe_be.backend.dto.response.LoginResponse;
import com.floginfe_be.backend.entity.User;
import com.floginfe_be.backend.repository.UserRepository;
import com.floginfe_be.backend.security.JwtTokenProvider;
import com.floginfe_be.backend.service.impl.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Login Service Unit Tests")
class AuthServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedpassword");
    }

    @Test
    @DisplayName("TC1: Login thành công với credentials hợp lệ")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("correctpass");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correctpass", "hashedpassword")).thenReturn(true);
        when(tokenProvider.generateToken(anyString())).thenReturn("jwt.token.here");

        LoginResponse response = authService.authenticate(request);

        assertTrue(response.isSuccess());
        assertEquals(AuthServiceImpl.MSG_LOGIN_SUCCESS, response.getMessage());
        assertNotNull(response.getToken());
    }

    @Test
    @DisplayName("TC2: Login thất bại với username sai")
    void testLoginFailureWrongUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("wronguser");
        request.setPassword("Pass123");

        when(userRepository.findByUsername("wronguser")).thenReturn(Optional.empty());

        LoginResponse response = authService.authenticate(request);

        assertFalse(response.isSuccess());
        assertEquals(AuthServiceImpl.MSG_INVALID_CREDENTIALS, response.getMessage());
        assertNull(response.getToken());
    }

    @Test
    @DisplayName("TC3: Login thất bại với password sai")
    void testLoginFailureWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpass");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hashedpassword")).thenReturn(false);

        LoginResponse response = authService.authenticate(request);

        assertFalse(response.isSuccess());
        assertEquals(AuthServiceImpl.MSG_INVALID_CREDENTIALS, response.getMessage());
        assertNull(response.getToken());
    }

    @Test
    @DisplayName("TC4: Validation username trống")
    void testValidateLoginUsernameEmpty() {
        String error = authService.validateLogin("", "password1");
        assertEquals(AuthServiceImpl.ERR_USERNAME_EMPTY, error);
    }

    @Test
    @DisplayName("TC5: Validation username chứa ký tự không hợp lệ")
    void testValidateLoginUsernameInvalidCharacters() {
        String error = authService.validateLogin("user_name", "password1");
        assertEquals(AuthServiceImpl.ERR_USERNAME_INVALID_CHARS, error);
    }

    @Test
    @DisplayName("TC6: Validation password trống")
    void testValidateLoginPasswordEmpty() {
        String error = authService.validateLogin("username", "");
        assertEquals(AuthServiceImpl.ERR_PASSWORD_EMPTY, error);
    }

    @Test
    @DisplayName("TC7: Validation password không có số")
    void testValidateLoginPasswordNoDigit() {
        String error = authService.validateLogin("username", "abcdef");
        assertEquals(AuthServiceImpl.ERR_PASSWORD_COMPLEXITY, error);
    }

    @Test
    @DisplayName("TC8: Validation password quá ngắn")
    void testValidateLoginPasswordTooShort() {
        String error = authService.validateLogin("username", "pass1");
        assertEquals(AuthServiceImpl.ERR_PASSWORD_LENGTH, error);
    }

    @Test
    @DisplayName("TC9: Validation username quá ngắn")
    void testValidateLoginUsernameTooShort() {
        String error = authService.validateLogin("ab", "password1");
        assertEquals(AuthServiceImpl.ERR_USERNAME_LENGTH, error);
    }


}
