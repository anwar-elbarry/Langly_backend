package com.langly.app.Authority.service.role;

import com.langly.app.Authority.web.dto.request.RoleRequest;
import com.langly.app.Authority.web.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest request);
    RoleResponse getById(String id);
    RoleResponse getByName(String name);
    List<RoleResponse> getAll();
    RoleResponse update(String id, RoleRequest request);
    void delete(String id);
}
