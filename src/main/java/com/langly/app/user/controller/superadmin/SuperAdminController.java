package com.langly.app.user.controller.superadmin;

import com.langly.app.user.service.superadmin.SuperAdminService;
import com.langly.app.user.web.dto.request.SuperAdminRequest;
import com.langly.app.user.web.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/super-admins")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody SuperAdminRequest request) {
        UserResponse response = superAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable String id) {
        UserResponse response = superAdminService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> responses = superAdminService.getAll();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable String id,
            @Valid @RequestBody SuperAdminRequest request) {
        UserResponse response = superAdminService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        superAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
