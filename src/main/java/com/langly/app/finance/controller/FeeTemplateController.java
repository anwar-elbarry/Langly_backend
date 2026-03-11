package com.langly.app.finance.controller;

import com.langly.app.finance.service.FeeTemplateService;
import com.langly.app.finance.web.dto.FeeTemplateRequest;
import com.langly.app.finance.web.dto.FeeTemplateResponse;
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
@RequestMapping("/api/v1/schools/{schoolId}/fee-templates")
@RequiredArgsConstructor
@Tag(name = "Fee Templates", description = "Catalogue de frais par école")
public class FeeTemplateController {

    private final FeeTemplateService feeTemplateService;

    @GetMapping
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Lister le catalogue de frais d'une école")
    public ResponseEntity<List<FeeTemplateResponse>> getAllBySchoolId(@PathVariable String schoolId) {
        return ResponseEntity.ok(feeTemplateService.getAllBySchoolId(schoolId));
    }

    @PostMapping
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Créer un modèle de frais")
    public ResponseEntity<FeeTemplateResponse> create(
            @PathVariable String schoolId,
            @Valid @RequestBody FeeTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feeTemplateService.create(schoolId, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Modifier un modèle de frais")
    public ResponseEntity<FeeTemplateResponse> update(
            @PathVariable String id,
            @Valid @RequestBody FeeTemplateRequest request) {
        return ResponseEntity.ok(feeTemplateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Supprimer un modèle de frais")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        feeTemplateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
