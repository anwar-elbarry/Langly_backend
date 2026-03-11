package com.langly.app.course.controller;

import com.langly.app.course.service.CourseService;
import com.langly.app.course.service.TeacherService;
import com.langly.app.course.web.dto.CourseResponse;
import com.langly.app.course.web.dto.TeacherOverviewResponse;
import com.langly.app.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
@Tag(name = "Teacher — Dashboard", description = "Tableau de bord et cours du professeur")
public class TeacherController {

    private final CourseService courseService;
    private final TeacherService teacherService;

    @GetMapping("/courses")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Mes cours", description = "Retourne les cours assignés au professeur connecté")
    public ResponseEntity<List<CourseResponse>> getMyCourses(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(courseService.getAllByTeacherId(user.getId()));
    }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Vue d'ensemble", description = "Statistiques du tableau de bord du professeur")
    public ResponseEntity<TeacherOverviewResponse> getOverview(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(teacherService.getOverview(user.getId()));
    }
}
