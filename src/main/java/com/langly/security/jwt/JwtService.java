package com.langly.security.jwt;

import com.langly.app.user.entity.User;
import com.langly.security.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String secret ;
    @Value("${jwt.expiration}") // 24 hours
    private long jwtExpiration;

    @Value("${jwt.refreshExpiration}") // 7 days
    private long refreshExpiration;


    private final TokenService tokenService;

    private final Set<String> blacklistedTokens = new HashSet<>();
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    @PostConstruct
    protected void init() {
        // Only encode once when the bean is created
        if (this.secret == null || this.secret.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured");
        }
        if (secret.length() < 32) {
            throw new IllegalStateException("JWT secret key must be at least 32 characters for HS256");
        }
    }

    public String generateAccessToken(User user) {
        return buildToken(user, jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        String refreshToken = buildToken(user, refreshExpiration);
        tokenService.storeRefreshToken(user.getUsername(), refreshToken, refreshExpiration / 1000);
        return refreshToken;
    }

    private String buildToken(User user, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        List<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        claims.put("authorities",authorities);

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SIGNATURE_ALGORITHM)
                .compact();
    }

    // Convert string secret → HMAC-SHA key
    private Key getSigningKey(){
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
    }


    public Claims extractAllClaims(String token) throws RuntimeException{
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

    public boolean validateToken(String token , UserDetails userDetails){
        final String email = extractAllClaims(token).getSubject();
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenBlackListed(String token){
        return blacklistedTokens.contains(token);
    }

    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7).trim();
            if (!jwt.isEmpty()) {
                blacklistedTokens.add(jwt);
            }
        }
    }
    public long getExpirationTime(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().getTime();
        } catch (Exception e) {
            throw new SecurityException("Invalid token: " + e.getMessage());
        }
    }
}
