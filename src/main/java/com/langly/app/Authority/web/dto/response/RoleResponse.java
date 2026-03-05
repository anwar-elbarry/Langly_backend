package com.langly.app.Authority.web.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class RoleResponse {
    private String id;
    private String name;
    private Set<PermissionResponse> permissions;
}
