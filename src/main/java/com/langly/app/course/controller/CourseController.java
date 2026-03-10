package com.langly.app.course.controller;

import com.langly.app.course.service.CourseService;
import com.langly.app.course.web.dto.CourseRequest;
import com.langly.app.course.web.dto.CourseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Gestion des cours — US-AD-03")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Creer un cours")
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER')")
    @Operation(summary = "Obtenir un cours par ID")
    public ResponseEntity<CourseResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    @GetMapping("/school/{schoolId}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER')")
    @Operation(summary = "Lister les cours d'une ecole")
    public ResponseEntity<List<CourseResponse>> getAllBySchoolId(@PathVariable String schoolId) {
        return ResponseEntity.ok(courseService.getAllBySchoolId(schoolId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Mettre a jour un cours")
    public ResponseEntity<CourseResponse> update(@PathVariable String id, @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Supprimer un cours")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
