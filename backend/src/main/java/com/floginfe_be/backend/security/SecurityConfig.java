package com.floginfe_be.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Định nghĩa Bean cho PasswordEncoder (sử dụng BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Tắt CSRF vì chúng ta dùng JWT (cho Stateless API)
                .csrf(AbstractHttpConfigurer::disable)
                // Cấu hình Authorization
                .authorizeHttpRequests(auth -> auth
                        // Cho phép tất cả truy cập vào endpoint Login
                        .requestMatchers("/api/auth/login").permitAll()
                        // Yêu cầu xác thực cho tất cả các request khác
                        .anyRequest().authenticated()
                );

        // (Cần bổ sung thêm JWT Filter nếu muốn bảo vệ các endpoint khác)
        return http.build();
    }
}