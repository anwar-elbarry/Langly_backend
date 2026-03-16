package com.langly.app.user.controller.user;

import com.langly.app.user.service.user.UserService;
import com.langly.app.user.web.dto.request.UpdatePasswordRequest;
import com.langly.app.user.web.dto.request.UserRequest;
import com.langly.app.user.web.dto.request.UserUpdateRequest;
import com.langly.app.user.web.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management API")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Creates a new user and sends an invitation email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "User with this email already exists")
    })
    @PostMapping
    @Override
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<UserResponse> getById(@PathVariable String id) {
        UserResponse response = userService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user by email", description = "Retrieves a user by their email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/email/{email}")
    @Override
    public ResponseEntity<UserResponse> getByEmail(@PathVariable String email) {
        UserResponse response = userService.getByEmail(email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all users", description = "Retrieves all users with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    @GetMapping
    @Override
    public ResponseEntity<Page<UserResponse>> getAll(Pageable pageable) {
        Page<UserResponse> response = userService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get users by school", description = "Retrieves all users belonging to a school")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "School not found")
    })
    @GetMapping("/school/{schoolId}")
    @Override
    public ResponseEntity<List<UserResponse>> getAllBySchoolId(@PathVariable String schoolId) {
        List<UserResponse> response = userService.getAllBySchoolId(schoolId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get users by role", description = "Retrieves all users with a specific role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @GetMapping("/role/{roleName}")
    @Override
    public ResponseEntity<List<UserResponse>> getAllByRole(@PathVariable String roleName) {
        List<UserResponse> response = userService.getAllByRole(roleName);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user", description = "Updates an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<UserResponse> update(@PathVariable String id, @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete user", description = "Deletes a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activate user", description = "Activates a suspended user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User activated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/activate")
    @Override
    public ResponseEntity<Void> activate(@PathVariable String id) {
        userService.activate(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Suspend user", description = "Suspends an active user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User suspended successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/suspend")
    @Override
    public ResponseEntity<Void> suspend(@PathVariable String id) {
        userService.suspend(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update password", description = "Updates the password of an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or current password is incorrect"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/password")
    @Override
    public ResponseEntity<Void> updatePassword(@PathVariable String id, @Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Upload profile image", description = "Uploads a profile image for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or file type"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping(value = "/{id}/profile-image", consumes = "multipart/form-data")
    @Override
    public ResponseEntity<UserResponse> uploadProfileImage(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        UserResponse response = userService.uploadProfileImage(id, file);
        return ResponseEntity.ok(response);
    }
}
