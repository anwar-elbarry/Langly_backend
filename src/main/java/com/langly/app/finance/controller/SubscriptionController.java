package com.langly.app.finance.controller;

import com.langly.app.finance.service.SubscriptionService;
import com.langly.app.finance.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "School subscription management API")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Create a new subscription for a school")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "School not found")
    })
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody SubscriptionRequest request) {
        SubscriptionResponse response = subscriptionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get subscription by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription found"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<SubscriptionResponse> getById(@PathVariable String id) {
        SubscriptionResponse response = subscriptionService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get subscriptions by school ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions found")
    })
    @GetMapping("/school/{schoolId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<List<SubscriptionResponse>> getBySchoolId(@PathVariable String schoolId) {
        List<SubscriptionResponse> response = subscriptionService.getBySchoolId(schoolId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all subscriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of subscriptions retrieved")
    })
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<SubscriptionResponse>> getAll() {
        List<SubscriptionResponse> response = subscriptionService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update subscription configuration (amount, currency, billing cycle)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<SubscriptionResponse> update(
            @PathVariable String id,
            @Valid @RequestBody SubscriptionUpdateRequest request) {
        SubscriptionResponse response = subscriptionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update payment status of a subscription (triggers school status update)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @PatchMapping("/{id}/payment-status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<SubscriptionResponse> updatePaymentStatus(
            @PathVariable String id,
            @Valid @RequestBody PaymentStatusUpdateRequest request) {
        SubscriptionResponse response = subscriptionService.updatePaymentStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscription deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        subscriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Initiate payment for a subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment initiated (returns checkout URL if Stripe)"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @PostMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<PaymentResponse> pay(
            @PathVariable String id,
            @Valid @RequestBody SelectPaymentMethodRequest request) {
        PaymentResponse response = subscriptionService.pay(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Declare a bank transfer payment for a subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer declared successfully"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @PostMapping("/{id}/declare-transfer")
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    public ResponseEntity<Void> declareTransfer(@PathVariable String id) {
        subscriptionService.declareTransfer(id);
        return ResponseEntity.ok().build();
    }
}
