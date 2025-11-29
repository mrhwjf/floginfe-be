package com.floginfe_be.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Ten dang nhap khong duoc de trong")
    @Size(min = 3, max = 50, message = "Ten dang nhap phai tu 3 den 50 ky tu")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Ten đang nhap chi co the chua chu cai va so")
    private String username;

    @NotBlank(message = "Mat khau khong duoc de trong")
    @Size(min = 6, max = 100, message = "Mat khau phai tu 6 den 100 ky tu")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "Mat khau phai chua it nhat mot chu cai va mot so")
    private String password;
}