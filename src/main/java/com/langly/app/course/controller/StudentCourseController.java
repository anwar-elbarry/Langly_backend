package com.langly.app.course.controller;

import com.langly.app.course.service.CourseService;
import com.langly.app.course.web.dto.ActiveCourseResponse;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.student.repository.StudentRepository;
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

/**
 * US03 : Endpoints dédiés à l'étudiant pour consulter ses cours.
 */
@RestController
@RequestMapping("/api/v1/student/courses")
@RequiredArgsConstructor
@Tag(name = "Student — Courses", description = "US03 : L'étudiant consulte ses cours actifs")
public class StudentCourseController {

    private final CourseService courseService;
    private final StudentRepository studentRepository;

    @GetMapping("/active")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mes cours actifs",
               description = "Retourne les cours actifs de l'étudiant connecté avec les prochaines sessions (titre, prof, horaires, salle/lien meeting)")
    public ResponseEntity<List<ActiveCourseResponse>> getMyActiveCourses(@AuthenticationPrincipal User user) {
        String studentId = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + user.getId()))
                .getId();

        return ResponseEntity.ok(courseService.getActiveCoursesForStudent(studentId));
    }
}
