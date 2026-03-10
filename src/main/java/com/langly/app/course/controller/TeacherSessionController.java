package com.langly.app.course.controller;

import com.langly.app.course.service.AttendanceService;
import com.langly.app.course.web.dto.AttendanceResponse;
import com.langly.app.course.web.dto.QrCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Teacher — Sessions", description = "US05 : Gestion de la présence par QR code")
public class TeacherSessionController {

    private final AttendanceService attendanceService;

    @PostMapping("/{sessionId}/qr")
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Générer un QR code", description = "Génère un QR token pour une session (valide 5 minutes)")
    public ResponseEntity<QrCodeResponse> generateQr(@PathVariable String sessionId) {
        return ResponseEntity.ok(attendanceService.generateQr(sessionId));
    }

    @GetMapping("/{sessionId}/attendance")
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Liste de présence", description = "Retourne la liste des étudiants présents pour une session")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(@PathVariable String sessionId) {
        return ResponseEntity.ok(attendanceService.getAttendanceBySession(sessionId));
    }
}
