package com.langly.app.user.service.superadmin;

import com.langly.app.user.web.dto.request.SuperAdminRequest;
import com.langly.app.user.web.dto.response.UserResponse;

import java.util.List;

public interface SuperAdminService {
    UserResponse create(SuperAdminRequest request);
    UserResponse getById(String id);
    List<UserResponse> getAll();
    UserResponse update(String id, SuperAdminRequest request);
    void delete(String id);
}
