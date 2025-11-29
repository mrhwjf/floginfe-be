package com.floginfe_be.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.security.Key;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenProvider {

    // 1. Inject giá trị Secret Key từ cấu hình
    @Value("${jwt.secret}")
    private String jwtSecret;

    // 2. Inject thời gian hết hạn từ cấu hình
    @Value("${jwt.expire}")
    private long jwtExpirationMs;

    // Đã loại bỏ các giá trị hardcode (key và jwtExpirationMs)

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // 3. Tạo Key từ chuỗi Secret đã được inject
        // Sử dụng getBytes() để chuyển chuỗi thành mảng byte, sau đó tạo Key HS512.
        // Chuỗi secret 64 ký tự đã đủ an toàn cho HS512
        Key signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS512) // Sử dụng signingKey
                .compact();
    }
}