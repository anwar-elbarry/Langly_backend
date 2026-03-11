package com.langly.app.finance.controller;

import com.langly.app.finance.service.BillingSettingService;
import com.langly.app.finance.web.dto.BillingSettingRequest;
import com.langly.app.finance.web.dto.BillingSettingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schools/{schoolId}/billing-settings")
@RequiredArgsConstructor
@Tag(name = "Billing Settings", description = "Paramètres de facturation par école")
public class BillingSettingController {

    private final BillingSettingService billingSettingService;

    @GetMapping
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Récupérer les paramètres de facturation d'une école")
    public ResponseEntity<BillingSettingResponse> getBySchoolId(@PathVariable String schoolId) {
        return ResponseEntity.ok(billingSettingService.getBySchoolId(schoolId));
    }

    @PutMapping
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Mettre à jour les paramètres de facturation")
    public ResponseEntity<BillingSettingResponse> update(
            @PathVariable String schoolId,
            @Valid @RequestBody BillingSettingRequest request) {
        return ResponseEntity.ok(billingSettingService.update(schoolId, request));
    }
}
