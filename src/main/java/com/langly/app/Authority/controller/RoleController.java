package com.langly.app.Authority.controller;

import com.langly.app.Authority.service.role.RoleService;
import com.langly.app.Authority.web.dto.request.RoleRequest;
import com.langly.app.Authority.web.dto.response.RoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleRequest request) {
        RoleResponse response = roleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getById(@PathVariable String id) {
        RoleResponse response = roleService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<RoleResponse> getByName(@PathVariable String name) {
        RoleResponse response = roleService.getByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAll() {
        List<RoleResponse> responses = roleService.getAll();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> update(
            @PathVariable String id,
            @Valid @RequestBody RoleRequest request) {
        RoleResponse response = roleService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<RoleResponse> assignPermissions(
            @PathVariable String roleId,
            @RequestBody List<String> permissionIds) {
        RoleResponse response = roleService.assignPermissions(roleId, permissionIds);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{roleId}/permissions")
    public ResponseEntity<RoleResponse> takePermissions(
            @PathVariable String roleId,
            @RequestBody List<String> permissionIds) {
        RoleResponse response = roleService.takePermission(roleId, permissionIds);
        return ResponseEntity.ok(response);
    }
}
