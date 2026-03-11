package com.langly.app.finance.controller;

import com.langly.app.finance.service.InvoiceService;
import com.langly.app.finance.web.dto.CreateInstallmentPlanRequest;
import com.langly.app.finance.web.dto.InvoiceResponse;
import com.langly.app.finance.web.dto.PaymentScheduleResponse;
import com.langly.app.finance.web.dto.RecordPaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Invoices — Admin", description = "Gestion des factures (admin école)")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/api/v1/schools/{schoolId}/invoices")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Lister les factures d'une école")
    public ResponseEntity<List<InvoiceResponse>> getAllBySchoolId(@PathVariable String schoolId) {
        return ResponseEntity.ok(invoiceService.getAllBySchoolId(schoolId));
    }

    @GetMapping("/api/v1/invoices/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Détail d'une facture")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @PostMapping("/api/v1/invoices/{id}/payments")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Enregistrer un paiement sur une facture")
    public ResponseEntity<InvoiceResponse> recordPayment(
            @PathVariable String id,
            @Valid @RequestBody RecordPaymentRequest request) {
        return ResponseEntity.ok(invoiceService.recordPayment(id, request));
    }

    @GetMapping("/api/v1/invoices/{id}/schedule")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Voir le plan d'échelonnement d'une facture")
    public ResponseEntity<List<PaymentScheduleResponse>> getSchedule(@PathVariable String id) {
        return ResponseEntity.ok(invoiceService.getSchedule(id));
    }

    @PostMapping("/api/v1/invoices/{id}/schedule")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Créer un plan d'échelonnement pour une facture")
    public ResponseEntity<List<PaymentScheduleResponse>> createInstallmentPlan(
            @PathVariable String id,
            @Valid @RequestBody CreateInstallmentPlanRequest request) {
        return ResponseEntity.ok(invoiceService.createInstallmentPlan(id, request.getPlan()));
    }
}
