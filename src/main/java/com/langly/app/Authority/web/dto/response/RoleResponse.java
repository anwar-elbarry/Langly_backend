package com.langly.app.Authority.web.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class RoleResponse {
    private String id;
    private String name;
}
