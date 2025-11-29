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
        assertEquals("Ten dang nhap khong duoc de trong", validationError);
    }

    @Test
    @DisplayName("TC5: Login voi password null")
    void testLoginWithNullPassword() {
        // Arrange
        String validationError = authService.validateLogin("testuser", null);

        // Assert
        assertFalse(validationError.isEmpty());
        assertEquals("Mat khau khong duoc de trong", validationError);
    }

    @Test
    @DisplayName("TC6: Login voi username va password null")
    void testLoginWithBothNull() {
        // Act
        String validationError = authService.validateLogin(null, null);

        // Assert
        assertFalse(validationError.isEmpty());
        assertEquals("Ten dang nhap khong duoc de trong", validationError);
    }



    // b) Test validation methods riêng lẻ (1 điểm)
    @Test
    @DisplayName("Validation: Username trong - nen tra ve loi")
    void testValidateEmptyUsername() {
        // Act
        String result = authService.validateLogin("", "Pass123");

        // Assert
        assertEquals("Ten dang nhap khong duoc de trong", result);
    }

    @Test
    @DisplayName("Validation: Username chi co space - nen tra ve loi")
    void testValidateWhitespaceUsername() {
        // Act
        String result = authService.validateLogin("   ", "Pass123");

        // Assert
        assertEquals("Ten dang nhap khong duoc de trong", result);
    }

    @Test
    @DisplayName("Validation: Username qua ngan (< 3 ky tu)")
    void testValidateUsernameTooShort() {
        // Act
        String result = authService.validateLogin("ab", "Pass123");

        // Assert
        assertEquals("Ten dang nhap phai tu 3 den 50 ky tu", result);
    }

    @Test
    @DisplayName("Validation: Username qua dai (> 50 ky tu)")
    void testValidateUsernameTooLong() {
        // Act
        String username = "a".repeat(51);
        String result = authService.validateLogin(username, "Pass123");

        // Assert
        assertEquals("Ten dang nhap phai tu 3 den 50 ky tu", result);
    }

    @Test
    @DisplayName("Validation: Username co ky tu dac biet khong hop le")
    void testValidateUsernameInvalidCharacters() {
        // Act
        String result = authService.validateLogin("user@name", "Pass123");

        // Assert
        assertEquals("Ten dang nhap chi co the chua chu cai va so", result);
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
        assertEquals("Mat khau khong duoc de trong", result);
    }

    @Test
    @DisplayName("Validation: Password chi co space - nen tra ve loi")
    void testValidateWhitespacePassword() {
        // Act
        String result = authService.validateLogin("testuser", "   ");

        // Assert
        assertEquals("Mat khau khong duoc de trong", result);
    }

    @Test
    @DisplayName("Validation: Password qua ngan (< 6 ky tu)")
    void testValidatePasswordTooShort() {
        // Act
        String result = authService.validateLogin("testuser", "Pass1");

        // Assert
        assertEquals("Mat khau phai tu 6 den 100 ky tu", result);
    }

    @Test
    @DisplayName("Validation: Password qua dai (> 100 ky tu)")
    void testValidatePasswordTooLong() {
        // Act
        String password = "a".repeat(101);
        String result = authService.validateLogin("testuser", password);

        // Assert
        assertEquals("Mat khau phai tu 6 den 100 ky tu", result);
    }

    @Test
    @DisplayName("Validation: Password khong co chu cai")
    void testValidatePasswordNoLetters() {
        // Act
        String result = authService.validateLogin("testuser", "123456");

        // Assert
        assertEquals("Mat khau phai chua it nhat mot chu cai va mot so", result);
    }

    @Test
    @DisplayName("Validation: Password khong co chu so")
    void testValidatePasswordNoDigits() {
        // Act
        String result = authService.validateLogin("testuser", "Password");

        // Assert
        assertEquals("Mat khau phai chua it nhat mot chu cai va mot so", result);
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


    // c) Additional Edge Cases for Coverage >= 85%
    @Test
    @DisplayName("Edge Case: Username voi do dai toi thieu (3 ky tu)")
    void testValidateUsernameMinimumLength() {
        // Act
        String result = authService.validateLogin("abc", "Pass123");

        // Assert
        assertEquals("", result);
    }

    @Test
    @DisplayName("Edge Case: Username voi do dai toi da (50 ky tu)")
    void testValidateUsernameMaximumLength() {
        // Act
        String username = "a".repeat(50);
        String result = authService.validateLogin(username, "Pass123");

        // Assert
        assertEquals("", result);
    }

    @Test
    @DisplayName("Edge Case: Password voi do dai toi thieu (6 ky tu)")
    void testValidatePasswordMinimumLength() {
        // Act
        String result = authService.validateLogin("testuser", "Pass12");

        // Assert
        assertEquals("", result);
    }

    @Test
    @DisplayName("Edge Case: Password voi do dai toi da (100 ky tu)")
    void testValidatePasswordMaximumLength() {
        // Act
        String password = "a1" + "b".repeat(98);
        String result = authService.validateLogin("testuser", password);

        // Assert
        assertEquals("", result);
    }

    @Test
    @DisplayName("Edge Case: Username chi chua so")
    void testValidateUsernameOnlyNumbers() {
        // Act
        String result = authService.validateLogin("12345", "Pass123");

        // Assert
        assertEquals("", result);
    }

    @Test
    @DisplayName("Edge Case: Username chi chua chu cai")
    void testValidateUsernameOnlyLetters() {
        // Act
        String result = authService.validateLogin("abcdef", "Pass123");

        // Assert
        assertEquals("", result);
    }

    @Test
    @DisplayName("Edge Case: Password co ky tu dac biet (van hop le)")
    void testValidatePasswordWithSpecialCharacters() {
        // Act
        String result = authService.validateLogin("testuser", "Pass@123!");

        // Assert
        assertEquals("", result);
    }

    @Test
    @DisplayName("Integration: Authenticate voi user ton tai nhung password null trong DB")
    void testAuthenticateWithNullPasswordInDatabase() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Pass123");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword(null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("Pass123", null)).thenReturn(false);

        // Act
        LoginResponse response = authService.authenticate(request);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Invalid username or password", response.getMessage());
    }

    @Test
    @DisplayName("Integration: Multiple authentication attempts")
    void testMultipleAuthenticationAttempts() {
        // Arrange
        LoginRequest request1 = new LoginRequest();
        request1.setUsername("user1");
        request1.setPassword("Pass123");

        LoginRequest request2 = new LoginRequest();
        request2.setUsername("user2");
        request2.setPassword("Pass456");

        User mockUser1 = new User();
        mockUser1.setUsername("user1");
        mockUser1.setPassword("$2a$10$hash1");

        User mockUser2 = new User();
        mockUser2.setUsername("user2");
        mockUser2.setPassword("$2a$10$hash2");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(mockUser1));
        when(userRepository.findByUsername("user2")).thenReturn(Optional.of(mockUser2));
        when(passwordEncoder.matches("Pass123", "$2a$10$hash1")).thenReturn(true);
        when(passwordEncoder.matches("Pass456", "$2a$10$hash2")).thenReturn(true);

        // Act
        LoginResponse response1 = authService.authenticate(request1);
        LoginResponse response2 = authService.authenticate(request2);

        // Assert
        assertTrue(response1.isSuccess());
        assertTrue(response2.isSuccess());
        verify(userRepository, times(1)).findByUsername("user1");
        verify(userRepository, times(1)).findByUsername("user2");
    }
}
