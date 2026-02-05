package com.langly.app.user.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRequest {

    @Size(max = 25)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @NotNull
    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    @Pattern(regexp = "^[+0-9().\\-\\s]{7,20}$", message = "phone number format is invalid")
    private String phoneNumber;

    @Size(max = 255)
    private String profile;

    @NotNull
    private String roleId;

    @NotNull
    private String schoolId;
}
