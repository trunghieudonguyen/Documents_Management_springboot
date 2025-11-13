package com.example.documentsmanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 🔑 Khóa bí mật (thay bằng chuỗi mạnh hơn trong môi trường thực)
    private static final String SECRET_KEY = "my-super-secret-key-for-jwt-authentication-1234567890";

    // ⏰ Thời gian hết hạn token: 1 giờ
    private static final long EXPIRATION_TIME = 60 * 60 * 1000;

    private final Key key;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /** ✅ Sinh token JWT */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** ✅ Lấy username từ token */
    public String extractUsername(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    /** ✅ Kiểm tra token hợp lệ */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
