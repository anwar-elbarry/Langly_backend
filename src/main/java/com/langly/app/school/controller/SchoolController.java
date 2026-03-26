package com.langly.app.school.controller;

import com.langly.app.school.service.SchoolService;
import com.langly.app.school.web.dto.SchoolRequest;
import com.langly.app.school.web.dto.SchoolResponse;
import com.langly.app.school.web.dto.SchoolUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
@Tag(name = "Schools", description = "School management API")
public class SchoolController {

    private final SchoolService schoolService;

    @Operation(summary = "Create a new school")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "School created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<SchoolResponse> create(@Valid @RequestBody SchoolRequest request) {
        SchoolResponse response = schoolService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get school by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "School found"),
            @ApiResponse(responseCode = "404", description = "School not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SchoolResponse> getById(@PathVariable String id) {
        SchoolResponse response = schoolService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search schools by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schools found")
    })
    @GetMapping("/search")
    public ResponseEntity<List<SchoolResponse>> getByName(@RequestParam String name) {
        List<SchoolResponse> response = schoolService.getByName(name);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all schools")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of schools retrieved")
    })
    @GetMapping
    public ResponseEntity<List<SchoolResponse>> getAll() {
        List<SchoolResponse> response = schoolService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update a school")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "School updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "School not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SchoolResponse> update(
            @PathVariable String id,
            @Valid @RequestBody SchoolUpdateRequest request) {
        SchoolResponse response = schoolService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Upload school logo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logo uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file"),
            @ApiResponse(responseCode = "404", description = "School not found")
    })
    @PostMapping(value = "/{id}/logo", consumes = "multipart/form-data")
    public ResponseEntity<SchoolResponse> uploadLogo(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        SchoolResponse response = schoolService.uploadLogo(id, file);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a school")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "School deleted successfully"),
            @ApiResponse(responseCode = "404", description = "School not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        schoolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
