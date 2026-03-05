package com.langly.app.user.web.dto.response;

import com.langly.app.Authority.entity.Role;
import com.langly.app.email.EmailPreview;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String profile;

    private Role role;

    private String schoolId;

    // Only populated in dev mode (app.mail.enabled=false) — null in production
    private EmailPreview emailPreview;
}
