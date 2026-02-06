package com.langly.app.Authority.service.permission;

import com.langly.app.Authority.web.dto.response.PermissionResponse;
import java.util.List;

public interface PermissionService {
    PermissionResponse create(String name);
    List<PermissionResponse> createMulti(List<String> name);
    PermissionResponse getById(String id);
    List<PermissionResponse> getAll();
    void delete(String id);
}
