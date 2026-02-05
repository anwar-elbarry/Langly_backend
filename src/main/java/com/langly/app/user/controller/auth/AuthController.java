package com.langly.app.user.controller.auth;

import com.langly.app.user.web.dto.auth.AuthRequest;
import com.langly.app.user.web.dto.auth.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthController {
 ResponseEntity<AuthResponse> login(AuthRequest request);
 ResponseEntity<Void> logout(HttpServletRequest request);
 ResponseEntity<Map<String,String>> refreshToken(String token);
}
