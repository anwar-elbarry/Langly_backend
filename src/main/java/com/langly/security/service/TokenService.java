package com.langly.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String BLACKLIST_TOKEN_PREFIX = "blacklist_token:";

    private final StringRedisTemplate redisTemplate;


    // Store refresh token with user details
    public void storeRefreshToken(String email, String refreshToken, long expirationInSeconds) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(key, refreshToken, expirationInSeconds, TimeUnit.SECONDS);
    }

    // Get refresh token for user
    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + email);
    }

    // Delete refresh token
    public void deleteRefreshToken(String email) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + email);
    }

    // Blacklist token
    public void blacklistToken(String token, long expirationInSeconds) {
        String key = BLACKLIST_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expirationInSeconds, TimeUnit.SECONDS);
    }

    // Check if token is blacklisted
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_TOKEN_PREFIX + token));
    }
}
