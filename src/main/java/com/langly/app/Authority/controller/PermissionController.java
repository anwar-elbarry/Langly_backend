package com.langly.app.Authority.controller;

import com.langly.app.Authority.service.permission.PermissionService;
import com.langly.app.Authority.web.dto.request.PermissionRequest;
import com.langly.app.Authority.web.dto.response.PermissionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Permission management API")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Create a new permission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Permission created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<PermissionResponse> create(@Valid @RequestBody PermissionRequest request) {
        PermissionResponse response = permissionService.create(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Create multiple permissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Permissions created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/batch")
    public ResponseEntity<List<PermissionResponse>> createMulti(@RequestBody List<String> names) {
        List<PermissionResponse> response = permissionService.createMulti(names);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get permission by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permission found"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getById(@PathVariable String id) {
        PermissionResponse response = permissionService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all permissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of permissions retrieved")
    })
    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAll() {
        List<PermissionResponse> response = permissionService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a permission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Permission deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
