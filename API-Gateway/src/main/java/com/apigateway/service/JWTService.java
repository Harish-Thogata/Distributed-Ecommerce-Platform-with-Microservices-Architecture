package com.apigateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.secret}") // Read from application.yml
    private String secretKey;

    // Validate the JWT token (Check Signature & Expiry) reactively
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                Claims claims = extractAllClaims(token); // This will fail if signature is invalid
                return !isTokenExpired(claims);
            } catch (Exception ex) {
                return false;
            }
        }).subscribeOn(Schedulers.boundedElastic()); // Offload blocking call
    }

    // Check if the token is expired
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    // Extract claims from token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Convert Base64 Secret Key to SecretKey object
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public Mono<Claims> getClaims(String token) {
        return Mono.fromCallable(() -> {
            try {
                return extractAllClaims(token);
            } catch (Exception e) {
                throw new RuntimeException("Token claims extraction failed", e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
