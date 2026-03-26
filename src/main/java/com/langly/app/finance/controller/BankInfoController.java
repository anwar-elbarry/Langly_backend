package com.langly.app.finance.controller;

import com.langly.app.finance.service.BankInfoService;
import com.langly.app.finance.web.dto.BankInfoResponse;
import com.langly.app.finance.web.dto.BankInfoUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bank-info")
@RequiredArgsConstructor
@Tag(name = "Bank Info", description = "Manage bank transfer information")
public class BankInfoController {

    private final BankInfoService bankInfoService;

    @Operation(summary = "Get current bank info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bank info retrieved")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<BankInfoResponse> get() {
        return ResponseEntity.ok(bankInfoService.get());
    }

    @Operation(summary = "Update bank info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bank info updated")
    })
    @PutMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<BankInfoResponse> update(@Valid @RequestBody BankInfoUpdateRequest request) {
        return ResponseEntity.ok(bankInfoService.update(request));
    }
}
