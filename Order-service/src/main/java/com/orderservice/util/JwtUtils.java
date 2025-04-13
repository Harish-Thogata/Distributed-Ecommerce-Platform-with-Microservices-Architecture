package com.orderservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    
    private final SecretKey secretKey;

    public JwtUtils(@Value("${jwt.secret}") String secretString) {
        // Decode Base64 encoded secret
        byte[] keyBytes = Base64.getDecoder().decode(secretString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String extractUserEmail(String token) {
        try {
            String jwt = token.replace("Bearer ", "").trim();
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            return claims.getSubject();
        } catch (SecurityException e) {
            logger.error("JWT signature validation failed: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        } catch (Exception e) {
            logger.error("JWT validation error: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}
