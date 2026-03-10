package com.langly.app.course.controller;

import com.langly.app.course.service.AttendanceService;
import com.langly.app.course.web.dto.AttendanceResponse;
import com.langly.app.course.web.dto.MarkAttendanceRequest;
import com.langly.app.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * US05 : L'étudiant scanne un QR code pour marquer sa présence.
 */
@RestController
@RequestMapping("/api/v1/student/attendance")
@RequiredArgsConstructor
@Tag(name = "Student — Attendance", description = "US05 : Présence via QR code")
public class StudentAttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Marquer ma présence", description = "L'étudiant scanne le QR code généré par le prof et marque sa présence")
    public ResponseEntity<AttendanceResponse> markAttendance(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody MarkAttendanceRequest request) {
        return ResponseEntity.ok(
                attendanceService.markPresent(user.getId(), request.getSessionId(), request.getQrToken()));
    }
}
