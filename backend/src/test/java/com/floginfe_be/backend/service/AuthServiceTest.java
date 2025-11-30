package com.floginfe_be.backend.service;

import com.floginfe_be.backend.dto.request.LoginRequest;
import com.floginfe_be.backend.dto.response.LoginResponse;
import com.floginfe_be.backend.entity.User;
import com.floginfe_be.backend.repository.UserRepository;
import com.floginfe_be.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Login Service Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, passwordEncoder);
    }


    // a) Test method authenticate() với các scenarios (3 điểm)
    @Test
    @DisplayName("TC1: Login thanh cong voi credentials hop le")
    void testLoginSuccess() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("$2a$10$hashedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("Test123", "$2a$10$hashedPassword")).thenReturn(true);

        // Act
        LoginResponse response = authService.authenticate(request);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Login successful", response.getMessage());
        
        // Verify interactions
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("Test123", "$2a$10$hashedPassword");
    }

    @Test
    @DisplayName("TC2: Login that bai voi username khong ton tai")
    void testLoginFailureUserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("wronguser");
        request.setPassword("Pass123");

        when(userRepository.findByUsername("wronguser")).thenReturn(Optional.empty());

        // Act
        LoginResponse response = authService.authenticate(request);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Invalid username or password", response.getMessage());
        
        // Verify passwordEncoder không được gọi khi user không tồn tại
        verify(userRepository, times(1)).findByUsername("wronguser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("TC3: Login that bai voi password sai")
    void testLoginFailureWrongPassword() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("WrongPass123");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("$2a$10$hashedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("WrongPass123", "$2a$10$hashedPassword")).thenReturn(false);

        // Act
        LoginResponse response = authService.authenticate(request);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Invalid username or password", response.getMessage());
        
        // Verify interactions
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("WrongPass123", "$2a$10$hashedPassword");
    }

    @Test
    @DisplayName("TC4: Login voi username null")
    void testLoginWithNullUsername() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername(null);
        request.setPassword("Pass123");

        // Act
        String validationError = authService.validateLogin(null, "Pass123");

        // Assert
        assertFalse(validationError.isEmpty());
        assertEquals("Username is required", validationError);
    }

    @Test
    @DisplayName("TC5: Login voi password null")
    void testLoginWithNullPassword() {
        // Arrange
        String validationError = authService.validateLogin("testuser", null);

        // Assert
        assertFalse(validationError.isEmpty());
        assertEquals("Password is required", validationError);
    }

    @Test
    @DisplayName("TC6: Login voi username va password null")
    void testLoginWithBothNull() {
        // Act
        String validationError = authService.validateLogin(null, null);

        // Assert
        assertFalse(validationError.isEmpty());
        assertEquals("Username is required", validationError);
    }



    // b) Test validation methods riêng lẻ (1 điểm)
    @Test
    @DisplayName("Validation: Username trong - nen tra ve loi")
    void testValidateEmptyUsername() {
        // Act
        String result = authService.validateLogin("", "Pass123");

        // Assert
        assertEquals("Username is required", result);
    }


    @Test
    @DisplayName("Validation: Username qua ngan (< 3 ky tu)")
    void testValidateUsernameTooShort() {
        // Act
        String result = authService.validateLogin("ab", "Pass123");

        // Assert
        assertEquals("Username must be between 3 and 50 characters", result);
    }

    @Test
    @DisplayName("Validation: Username qua dai (> 50 ky tu)")
    void testValidateUsernameTooLong() {
        // Act
        String username = "a".repeat(51);
        String result = authService.validateLogin(username, "Pass123");

        // Assert
        assertEquals("Username must be between 3 and 50 characters", result);
    }

    @Test
    @DisplayName("Validation: Username co ky tu dac biet khong hop le")
    void testValidateUsernameInvalidCharacters() {
        // Act
        String result = authService.validateLogin("user@name", "Pass123");

        // Assert
        assertEquals("Invalid username format", result);
    }

    @Test
    @DisplayName("Validation: Username hop le - khong co loi")
    void testValidateUsernameValid() {
        // Act
        String result = authService.validateLogin("validUser123", "Pass123");

        // Assert
        assertEquals("", result);
    }

    @Test
    @DisplayName("Validation: Password trong - nen tra ve loi")
    void testValidateEmptyPassword() {
        // Act
        String result = authService.validateLogin("testuser", "");

        // Assert
        assertEquals("Password is required", result);
    }


    @Test
    @DisplayName("Validation: Password qua ngan (< 6 ky tu)")
    void testValidatePasswordTooShort() {
        // Act
        String result = authService.validateLogin("testuser", "Pass1");

        // Assert
        assertEquals("Password must be between 6 and 100 characters", result);
    }

    @Test
    @DisplayName("Validation: Password qua dai (> 100 ky tu)")
    void testValidatePasswordTooLong() {
        // Act
        String password = "a".repeat(101);
        String result = authService.validateLogin("testuser", password);

        // Assert
        assertEquals("Password must be between 6 and 100 characters", result);
    }

    @Test
    @DisplayName("Validation: Password khong co chu cai")
    void testValidatePasswordNoLetters() {
        // Act
        String result = authService.validateLogin("testuser", "123456");

        // Assert
        assertEquals("Password must contain at least one letter and one number", result);
    }

    @Test
    @DisplayName("Validation: Password khong co chu so")
    void testValidatePasswordNoDigits() {
        // Act
        String result = authService.validateLogin("testuser", "Password");

        // Assert
        assertEquals("Password must contain at least one letter and one number", result);
    }

    @Test
    @DisplayName("Validation: Password hop le - khong co loi")
    void testValidatePasswordValid() {
        // Act
        String result = authService.validateLogin("testuser", "Pass123");

        // Assert
        assertEquals("", result);
    }

    @Test
    @DisplayName("Validation: Tat ca deu hop le - khong co loi")
    void testValidateAllValid() {
        // Act
        String result = authService.validateLogin("validUser123", "ValidPass123");

        // Assert
        assertTrue(result.isEmpty());
        assertEquals("", result);
    }

}
