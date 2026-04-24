package com.example.server.security;

import com.example.server.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.ValidationException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(String secretKey, long expiration){
if (secretKey == null || secretKey.isBlank()){
    throw new ValidationException("JWT secret must not be null or blank");
}
if(secretKey.length() < 32){
    throw new ValidationException("JWT secret must be at least 32 characters long");
}
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(User user){
        Date now = new Date();
        Date expire = new Date(now.getTime()+expiration);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", user.getName())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expire)
                .signWith(secretKey)
                .compact();
    }

    public boolean isValid(String token){
        try {
            extractAllClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException ex){
            return false;
        }
    }

    public String extractUserEmail(String token){
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserIs(String token){
        Object userId = extractAllClaims(token).get("userId");

        if(userId instanceof Long longValue){
            return longValue;
        }

        if(userId instanceof Integer intValue){
            return intValue.longValue();
        }

        if(userId instanceof String stringValue){
            return Long.parseLong(stringValue);
        }

        return null;
    }

    public String extractUserName(String token){
     Object o = extractAllClaims(token).get("name");
     return o != null ? o.toString() : null;
    }

    public String extractUserRole(String token){
        Object o = extractAllClaims(token).get("role");
        return o != null ? o.toString() : null;
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
