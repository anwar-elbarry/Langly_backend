package com.langly.app.user.service.auth;

import com.langly.app.user.entity.User;
import com.langly.app.user.web.dto.auth.AuthRequest;
import com.langly.app.user.web.dto.auth.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    void logout(HttpServletRequest request);
    Map<String,String> generateTokens(User user);
    Map<String,String> refreshToken(String refreshToken);
}