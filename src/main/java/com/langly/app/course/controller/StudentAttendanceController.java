package com.langly.app.course.controller;

import com.langly.app.course.service.AttendanceService;
import com.langly.app.course.web.dto.*;
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

/**
 * US05 : L'étudiant scanne un QR code pour marquer sa présence et consulte ses stats.
 */
@RestController
@RequestMapping("/api/v1/student/attendance")
@RequiredArgsConstructor
@Tag(name = "Student — Attendance", description = "US05 : dashboard")
public class StudentAttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/my-summary")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mon résumé global de présence")
    public ResponseEntity<StudentAttendanceSummaryResponse> getGlobalSummary(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(attendanceService.getStudentGlobalSummary(user.getId()));
    }

    @GetMapping("/my-summary/courses")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mon résumé de présence par cours")
    public ResponseEntity<List<CourseAttendanceSummaryResponse>> getCourseSummaries(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(attendanceService.getStudentCourseSummaries(user.getId()));
    }
}
