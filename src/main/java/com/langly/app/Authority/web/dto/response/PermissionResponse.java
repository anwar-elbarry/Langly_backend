package com.langly.app.Authority.web.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionResponse {
    private String id;
    private String name;
}
