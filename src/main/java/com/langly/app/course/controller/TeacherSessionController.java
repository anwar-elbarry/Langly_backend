package com.langly.app.course.controller;

import com.langly.app.course.service.AttendanceService;
import com.langly.app.course.web.dto.AttendanceResponse;
import com.langly.app.course.web.dto.ManualAttendanceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * US05 : Le professeur génère un QR code et consulte la présence.
 */
@RestController
@RequestMapping("/api/v1/teacher/sessions")
@RequiredArgsConstructor
@Tag(name = "Teacher — Sessions", description = "US05 : Gestion de la présence")
public class TeacherSessionController {

    private final AttendanceService attendanceService;

    @GetMapping("/{sessionId}/attendance")
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Liste de présence", description = "Retourne la liste des étudiants présents pour une session")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(@PathVariable String sessionId) {
        return ResponseEntity.ok(attendanceService.getAttendanceBySession(sessionId));
    }

    @GetMapping("/{sessionId}/attendance/full")
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Liste complète de présence", description = "Retourne tous les inscrits avec leur statut de présence")
    public ResponseEntity<List<AttendanceResponse>> getFullAttendance(@PathVariable String sessionId) {
        return ResponseEntity.ok(attendanceService.getFullAttendanceBySession(sessionId));
    }

    @PutMapping("/{sessionId}/attendance")
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Marquer la présence manuellement", description = "Le professeur marque la présence d'un étudiant")
    public ResponseEntity<AttendanceResponse> markManual(
            @PathVariable String sessionId,
            @Valid @RequestBody ManualAttendanceRequest request) {
        return ResponseEntity.ok(
                attendanceService.markManual(sessionId, request.getStudentId(), request.getStatus()));
    }
}
