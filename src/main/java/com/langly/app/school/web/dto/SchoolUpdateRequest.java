package com.langly.app.school.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SchoolUpdateRequest {
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(min = 3, max = 50, message = "Subdomain must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Subdomain can only contain lowercase letters, numbers, and hyphens")
    private String subDomain;

    private String logo;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;
}
