package com.langly.security.jwt;

import com.langly.security.service.TokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtService jwtService;
    private final TokenService tokenService;

    private final UserDetailsService userDetailsService;
    // In JwtAuthFiler.java
    @SuppressWarnings("unchecked")
    private Collection<? extends GrantedAuthority> extractAuthorities(Claims claims) {
        try {
            List<String> authorities = (List<String>) claims.get("authorities");
            if (authorities == null || authorities.isEmpty()) {
                return Collections.emptyList();
            }
            return authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error extracting authorities from token", e);
            return Collections.emptyList();
        }
    }
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authHeader.substring(7);


            // Check if token is blacklisted
            if (tokenService.isTokenBlacklisted(jwt)) {
                logger.warn("Attempted to use blacklisted token");
                sendErrorResponse(response, "Token has been invalidated", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Claims claims = jwtService.extractAllClaims(jwt);
            String email = claims.getSubject();

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (!jwtService.validateToken(jwt, userDetails)) {
                    sendErrorResponse(response, "Invalid token", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // Extract role from claims and create authorities
                Collection<? extends GrantedAuthority> authorities = extractAuthorities(claims);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            logger.error("Authentication error: {}", e.getMessage());
            sendErrorResponse(response, "Authentication failed: " + e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        response.getWriter().flush();
    }

}
