package com.langly.app.role.web.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoleResponse {
    private String id;
    private String name;
}
