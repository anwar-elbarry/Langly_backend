package com.langly.app.finance.controller;

import com.langly.app.finance.service.DiscountService;
import com.langly.app.finance.web.dto.DiscountRequest;
import com.langly.app.finance.web.dto.DiscountResponse;
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
@RequestMapping("/api/v1/schools/{schoolId}/discounts")
@RequiredArgsConstructor
@Tag(name = "Discounts", description = "Gestion des réductions par école")
public class DiscountController {

    private final DiscountService discountService;

    @GetMapping
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Lister les réductions d'une école")
    public ResponseEntity<List<DiscountResponse>> getAllBySchoolId(@PathVariable String schoolId) {
        return ResponseEntity.ok(discountService.getAllBySchoolId(schoolId));
    }

    @PostMapping
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Créer une réduction")
    public ResponseEntity<DiscountResponse> create(
            @PathVariable String schoolId,
            @Valid @RequestBody DiscountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discountService.create(schoolId, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Modifier une réduction")
    public ResponseEntity<DiscountResponse> update(
            @PathVariable String id,
            @Valid @RequestBody DiscountRequest request) {
        return ResponseEntity.ok(discountService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Supprimer une réduction")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        discountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
