package com.langly.app.user.web.dto.auth;

public record AuthRequest(
        String username,
        String password
) {
}
