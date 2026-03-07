package com.langly.app.user.web.dto.auth;

public record AuthRequest(
        String email,
        String password
) {
}
