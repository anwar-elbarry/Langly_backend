package com.langly.app.student.controller;

import com.langly.app.student.service.StudentService;
import com.langly.app.student.web.dto.AdminStudentUpdateRequest;
import com.langly.app.student.web.dto.StudentResponse;
import com.langly.app.student.web.dto.StudentUpdateRequest;
import com.langly.app.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Gestion des etudiants — US-AD-05")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get current student's profile")
    public ResponseEntity<StudentResponse> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studentService.getByUserId(user.getId()));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Student updates own profile (CNIE, birthDate)")
    public ResponseEntity<StudentResponse> updateMyProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StudentUpdateRequest request) {
        return ResponseEntity.ok(studentService.updateByStudent(user.getId(), request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER')")
    @Operation(summary = "Detail d'un etudiant")
    public ResponseEntity<StudentResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    @PatchMapping("/{id}/admin")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Admin updates student level and gender")
    public ResponseEntity<StudentResponse> updateByAdmin(
            @PathVariable String id,
            @Valid @RequestBody AdminStudentUpdateRequest request) {
        return ResponseEntity.ok(studentService.updateByAdmin(id, request));
    }

    @GetMapping("/school/{schoolId}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Tous les etudiants d'une ecole")
    public ResponseEntity<List<StudentResponse>> getAllBySchoolId(@PathVariable String schoolId) {
        return ResponseEntity.ok(studentService.getAllBySchoolId(schoolId));
    }

    @GetMapping("/school/{schoolId}/incomplete")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Profils incomplets — US-AD-05",
               description = "Retourne les etudiants dont CNIE, birthDate ou phoneNumber est manquant. Le champ missingFields liste les informations absentes.")
    public ResponseEntity<List<StudentResponse>> getIncompleteBySchoolId(@PathVariable String schoolId) {
        return ResponseEntity.ok(studentService.getIncompleteBySchoolId(schoolId));
    }
}
