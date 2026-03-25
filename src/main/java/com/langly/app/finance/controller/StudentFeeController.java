package com.langly.app.finance.controller;

import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.service.FeePaymentService;
import com.langly.app.finance.service.FeeTemplateService;
import com.langly.app.finance.web.dto.FeeTemplateResponse;
import com.langly.app.finance.web.dto.StudentFeeStatusResponse;
import com.langly.app.student.entity.Student;
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

@RestController
@RequestMapping("/api/v1/student/fees")
@RequiredArgsConstructor
@Tag(name = "Student — Fees", description = "Vue en lecture seule des frais pour l'étudiant")
public class StudentFeeController {

    private final FeePaymentService feePaymentService;
    private final FeeTemplateService feeTemplateService;
    private final StudentRepository studentRepository;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mes frais", description = "Retourne le statut de tous les frais actifs pour l'étudiant connecté")
    public ResponseEntity<List<StudentFeeStatusResponse>> getMyFees(@AuthenticationPrincipal User user) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + user.getId()));

        if (student.getUser() == null || student.getUser().getSchool() == null) {
            throw new IllegalStateException("School not found for student");
        }
        
        String schoolId = student.getUser().getSchool().getId();
        return ResponseEntity.ok(feePaymentService.getStudentFeeStatuses(schoolId, student.getId()));
    }

    @GetMapping("/catalog")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Catalogue des frais", description = "Retourne la liste des frais configurés par l'école")
    public ResponseEntity<List<FeeTemplateResponse>> getFeeCatalog(@AuthenticationPrincipal User user) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + user.getId()));

        if (student.getUser() == null || student.getUser().getSchool() == null) {
            throw new IllegalStateException("School not found for student");
        }
        
        String schoolId = student.getUser().getSchool().getId();
        return ResponseEntity.ok(feeTemplateService.getAllActiveBySchoolId(schoolId));
    }
}
