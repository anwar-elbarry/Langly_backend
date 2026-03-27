package com.langly.app.course.controller;

import com.langly.app.course.service.EnrollmentService;
import com.langly.app.course.web.dto.EnrollmentResponse;
import com.langly.app.course.web.dto.StudentEnrollmentRequest;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.entity.Billing;
import com.langly.app.finance.repository.BillingRepository;
import com.langly.app.student.entity.Student;
import com.langly.app.student.repository.StudentRepository;
import com.langly.app.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/student/enrollments")
@RequiredArgsConstructor
@Tag(name = "Student — Enrollments", description = "Demandes d'inscription étudiant")
public class StudentEnrollmentController {

    private final StudentRepository studentRepository;
    private final EnrollmentService enrollmentService;
    private final BillingRepository billingRepository;

    @PostMapping("/request")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Demander une inscription à un cours",
            description = "Crée une inscription avec statut PENDING_APPROVAL. L'admin doit approuver avant le paiement.")
    public ResponseEntity<EnrollmentResponse> requestEnrollment(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StudentEnrollmentRequest request) {

        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + user.getId()));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.requestEnrollment(student.getId(), request.getCourseId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mes inscriptions", description = "Retourne toutes les inscriptions de l'étudiant connecté")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
            @AuthenticationPrincipal User user) {

        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + user.getId()));

        return ResponseEntity.ok(enrollmentService.getAllByStudentId(student.getId()));
    }

    @GetMapping("/{billingId}/invoice")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Télécharger la facture PDF", description = "Retourne l'URL de la facture PDF pour un billing donné")
    public ResponseEntity<String> getInvoice(
            @AuthenticationPrincipal User user,
            @PathVariable String billingId) {

        Billing billing = billingRepository.findById(billingId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", billingId));

        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + user.getId()));

        if (!billing.getStudent().getId().equals(student.getId())) {
            throw new IllegalStateException("Ce paiement ne vous appartient pas");
        }

        if (billing.getInvoicePdfUrl() == null) {
            throw new ResourceNotFoundException("Invoice", billingId);
        }

        return ResponseEntity.ok(billing.getInvoicePdfUrl());
    }
}
