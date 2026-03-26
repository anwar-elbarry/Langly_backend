package com.langly.app.user.service.auth;

import com.langly.app.exception.AuthenticationException;
import com.langly.app.exception.UserNotFoundException;
import com.langly.app.user.entity.User;
import com.langly.app.user.repository.UserRepository;
import com.langly.app.user.web.dto.auth.AuthRequest;
import com.langly.app.user.web.dto.auth.AuthResponse;
import com.langly.app.user.web.dto.response.UserResponse;
import com.langly.app.user.web.mapper.UserMapper;
import com.langly.security.jwt.JwtService;
import com.langly.security.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            if (e instanceof DisabledException) {
                throw new AuthenticationException("Your account is suspended. Contact Langly SaaS team.");
            }
            throw new AuthenticationException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("this email: " +request.email()));

        UserResponse userRespDTO = userMapper.toResponse(user);
        Map<String, String> tokens = generateTokens(user);

        return AuthResponse.builder()
                .accessToken(tokens.get("accessToken"))
                .refreshToken(tokens.get("refreshToken"))
                .user(userRespDTO)
                .build();
    }

    @Override
    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtService.extractAllClaims(token).getSubject();
                long expiration = jwtService.getExpirationTime(token) - System.currentTimeMillis();
                if (expiration > 0) {
                    tokenService.blacklistToken(token, expiration / 1000);
                }
                // Delete refresh token
                tokenService.deleteRefreshToken(username);
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                throw new AuthenticationException("Error during logout: " + e.getMessage());
            }
        }
    }

    @Override
    public Map<String, String> generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    @Override
    public Map<String, String> refreshToken(String refreshToken) {
        String email = jwtService.extractAllClaims(refreshToken).getSubject();
        String storedRefreshToken = tokenService.getRefreshToken(email);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new AuthenticationException("Invalid or expired refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("with this email: "+email));

        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }
}
