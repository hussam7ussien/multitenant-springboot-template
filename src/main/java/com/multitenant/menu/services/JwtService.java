package com.multitenant.menu.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret:your-256-bit-secret-key-change-this-in-production-environment}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours default
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private Long refreshExpiration;

    @Value("${jwt.temp-session-expiration:600000}") // 10 minutes default
    private Long tempSessionExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    public String generateToken(String username, Map<String, Object> claims) {
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Generate temporary session token for OTP flow (10 min expiration)
     */
    public String generateTempSessionToken(String phone, String tenantId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", phone);
        claims.put("tenant_id", tenantId);
        claims.put("type", "temp_session");
        return createToken(claims, phone, tempSessionExpiration);
    }

    /**
     * Generate access token with user info and tenant
     */
    public String generateAccessToken(Long userId, String username, String phone, String tenantId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        claims.put("phone", phone);
        claims.put("tenant_id", tenantId);
        claims.put("type", "access");
        return createToken(claims, username, expiration);
    }

    /**
     * Generate refresh token (long-lived)
     */
    public String generateRefreshToken(Long userId, String username, String tenantId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        claims.put("tenant_id", tenantId);
        claims.put("type", "refresh");
        return createToken(claims, username, refreshExpiration);
    }

    /**
     * Create token with custom expiration
     */
    private String createToken(Map<String, Object> claims, String subject, Long expirationMs) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract tenant ID from token claims
     */
    public String extractTenantId(String token) {
        return extractClaim(token, claims -> claims.get("tenant_id", String.class));
    }

    /**
     * Extract user ID from token claims
     */
    public Long extractUserId(String token) {
        Object userIdObj = extractClaim(token, claims -> claims.get("user_id"));
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        return null;
    }

    /**
     * Extract phone from token claims
     */
    public String extractPhone(String token) {
        return extractClaim(token, claims -> claims.get("phone", String.class));
    }

    /**
     * Extract phone from temporary session token
     */
    public String extractPhoneFromTempToken(String token) {
        Claims claims = extractAllClaims(token);
        String type = claims.get("type", String.class);
        if ("temp_session".equals(type)) {
            return claims.get("phone", String.class);
        }
        return null;
    }

    /**
     * Extract tenant ID from temporary session token
     */
    public String extractTenantIdFromTempToken(String token) {
        Claims claims = extractAllClaims(token);
        String type = claims.get("type", String.class);
        if ("temp_session".equals(type)) {
            return claims.get("tenant_id", String.class);
        }
        return null;
    }

    /**
     * Validate refresh token
     */
    public Boolean validateRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String type = claims.get("type", String.class);
            if (!"refresh".equals(type)) {
                return false;
            }
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate temporary session token
     */
    public Boolean validateTempSessionToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String type = claims.get("type", String.class);
            if (!"temp_session".equals(type)) {
                return false;
            }
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}

