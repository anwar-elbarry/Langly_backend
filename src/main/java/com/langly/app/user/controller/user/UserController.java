package com.langly.app.user.controller.user;

import com.langly.app.user.web.dto.request.UpdatePasswordRequest;
import com.langly.app.user.web.dto.request.UserRequest;
import com.langly.app.user.web.dto.request.UserUpdateRequest;
import com.langly.app.user.web.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserController {
    ResponseEntity<UserResponse> create(UserRequest request);

    ResponseEntity<UserResponse> getById(String id);

    ResponseEntity<UserResponse> getByEmail(String email);

    ResponseEntity<Page<UserResponse>> getAll(Pageable pageable);

    ResponseEntity<List<UserResponse>> getAllBySchoolId(String schoolId);

    ResponseEntity<List<UserResponse>> getAllByRole(String roleName);

    ResponseEntity<UserResponse> update(String id, UserUpdateRequest request);

    ResponseEntity<Void> delete(String id);

    ResponseEntity<Void> activate(String id);

    ResponseEntity<Void> suspend(String id);

    ResponseEntity<Void> updatePassword(String id, UpdatePasswordRequest request);

    ResponseEntity<UserResponse> uploadProfileImage(String id, MultipartFile file);
}
