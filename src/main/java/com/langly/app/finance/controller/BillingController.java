package com.langly.app.finance.controller;

import com.langly.app.finance.service.BillingService;
import com.langly.app.finance.web.dto.BillingConfirmRequest;
import com.langly.app.finance.web.dto.BillingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/billings")
@RequiredArgsConstructor
@Tag(name = "Billings", description = "Gestion des paiements — US-AD-04")
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/school/{schoolId}/pending")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Paiements en attente d'une ecole")
    public ResponseEntity<List<BillingResponse>> getPendingBySchoolId(@PathVariable String schoolId) {
        return ResponseEntity.ok(billingService.getPendingBySchoolId(schoolId));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Confirmer un paiement manuel (CASH ou BANK_TRANSFER)")
    public ResponseEntity<BillingResponse> confirmPayment(@PathVariable String id,
                                                           @Valid @RequestBody BillingConfirmRequest request) {
        return ResponseEntity.ok(billingService.confirmPayment(id, request));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','TEACHER')")
    @Operation(summary = "Paiements d'un etudiant")
    public ResponseEntity<List<BillingResponse>> getAllByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(billingService.getAllByStudentId(studentId));
    }

    @GetMapping("/school/{schoolId}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Tous les paiements d'une ecole")
    public ResponseEntity<List<BillingResponse>> getAllBySchoolId(@PathVariable String schoolId) {
        return ResponseEntity.ok(billingService.getAllBySchoolId(schoolId));
    }
}
