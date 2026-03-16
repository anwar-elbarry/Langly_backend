package com.langly.app.user.service.user;

import com.langly.app.user.web.dto.request.UpdatePasswordRequest;
import com.langly.app.user.web.dto.request.UserRequest;
import com.langly.app.user.web.dto.request.UserUpdateRequest;
import com.langly.app.user.web.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserResponse create(UserRequest request);

    UserResponse getById(String id);

    UserResponse getByEmail(String email);

    Page<UserResponse> getAll(Pageable pageable);

    List<UserResponse> getAllBySchoolId(String schoolId);

    List<UserResponse> getAllByRole(String roleName);

        UserResponse update(String id, UserUpdateRequest request);

    void delete(String id);

    void activate(String id);

    void suspend(String id);

    void updatePassword(String id, UpdatePasswordRequest request);

    UserResponse uploadProfileImage(String id, MultipartFile file);
}
