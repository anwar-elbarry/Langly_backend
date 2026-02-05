package com.langly.app.user.web.dto.auth;

import com.langly.app.user.web.dto.response.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse user;
}