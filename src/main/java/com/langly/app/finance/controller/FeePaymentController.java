package com.langly.app.finance.controller;

import com.langly.app.finance.service.FeePaymentService;
import com.langly.app.finance.web.dto.FeePaymentRequest;
import com.langly.app.finance.web.dto.FeePaymentResponse;
import com.langly.app.finance.web.dto.StudentFeeStatusResponse;
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
@RequestMapping("/api/v1/schools/{schoolId}/fee-payments")
@RequiredArgsConstructor
@Tag(name = "Fee Payments", description = "Suivi des paiements de frais par les administrateurs")
public class FeePaymentController {

    private final FeePaymentService feePaymentService;

    @PostMapping
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Enregistrer un paiement de frais")
    public ResponseEntity<FeePaymentResponse> recordPayment(
            @PathVariable String schoolId,
            @Valid @RequestBody FeePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feePaymentService.recordPayment(schoolId, request));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Obtenir le statut des frais d'un élève")
    public ResponseEntity<List<StudentFeeStatusResponse>> getStudentFeeStatuses(
            @PathVariable String schoolId,
            @PathVariable String studentId) {
        return ResponseEntity.ok(feePaymentService.getStudentFeeStatuses(schoolId, studentId));
    }

    @GetMapping("/student/{studentId}/fee/{feeTemplateId}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Obtenir l'historique des paiements d'un élève pour un frais donné")
    public ResponseEntity<List<FeePaymentResponse>> getPaymentHistory(
            @PathVariable String schoolId,
            @PathVariable String studentId,
            @PathVariable String feeTemplateId) {
        return ResponseEntity.ok(feePaymentService.getPaymentHistory(schoolId, studentId, feeTemplateId));
    }

    @PutMapping("/close")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Clôturer un paiement de frais (pour les frais récurrents)")
    public ResponseEntity<Void> closeRecurringFee(
            @PathVariable String schoolId,
            @RequestParam String studentId,
            @RequestParam String feeTemplateId) {
        feePaymentService.closeRecurringFee(schoolId, studentId, feeTemplateId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Supprimer un enregistrement de paiement de frais")
    public ResponseEntity<Void> deletePayment(
            @PathVariable String schoolId,
            @PathVariable String id) {
        feePaymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
