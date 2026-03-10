package com.langly.app.course.controller;

import com.langly.app.course.service.EnrollmentService;
import com.langly.app.course.web.dto.EnrollmentRequest;
import com.langly.app.course.web.dto.EnrollmentResponse;
import com.langly.app.course.web.dto.UpdateEnrollmentStatusRequest;
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
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Inscription académique des étudiants — US-AD-02")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "Inscrire un étudiant à un cours", description = "Crée une inscription avec statut IN_PROGRESS et met à jour le niveau de l'étudiant")
    @PostMapping
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.enroll(request));
    }

    @Operation(summary = "Détail d'une inscription")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<EnrollmentResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(enrollmentService.getById(id));
    }

    @Operation(summary = "Inscriptions d'un étudiant")
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<List<EnrollmentResponse>> getAllByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(enrollmentService.getAllByStudentId(studentId));
    }

    @Operation(summary = "Inscriptions d'un cours")
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<List<EnrollmentResponse>> getAllByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(enrollmentService.getAllByCourseId(courseId));
    }

    @Operation(summary = "Toutes les inscriptions d'une école")
    @GetMapping("/school/{schoolId}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getAllBySchoolId(@PathVariable String schoolId) {
        return ResponseEntity.ok(enrollmentService.getAllBySchoolId(schoolId));
    }

    @Operation(summary = "Changer le statut d'une inscription", description = "US07 : Met à jour le statut (PASSED, FAILED, WITHDRAWN, etc.). Si PASSED → auto-génère un certificat PDF.")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<EnrollmentResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateEnrollmentStatusRequest request) {
        return ResponseEntity.ok(enrollmentService.updateStatus(id, request.getStatus()));
    }
}
