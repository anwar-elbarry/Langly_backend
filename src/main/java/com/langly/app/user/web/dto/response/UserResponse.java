package com.langly.app.user.web.dto.response;

import com.langly.app.user.entity.Role;
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
}
