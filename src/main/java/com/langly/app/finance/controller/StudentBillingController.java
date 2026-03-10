package com.langly.app.finance.controller;

import com.langly.app.course.entity.enums.EnrollmentStatus;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.entity.Billing;
import com.langly.app.finance.entity.enums.PaymentMethod;
import com.langly.app.finance.entity.enums.PaymentStatus;
import com.langly.app.finance.repository.BillingRepository;
import com.langly.app.finance.service.StripeService;
import com.langly.app.finance.web.dto.BillingResponse;
import com.langly.app.finance.web.dto.PaymentResponse;
import com.langly.app.finance.web.dto.SelectPaymentMethodRequest;
import com.langly.app.finance.web.mapper.BillingMapper;
import com.langly.app.student.entity.Student;
import com.langly.app.student.repository.StudentRepository;
import com.langly.app.user.entity.User;
import com.stripe.model.checkout.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Student-facing billing endpoints.
 * After admin approves enrollment, student selects payment method here.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/student/billings")
@RequiredArgsConstructor
@Tag(name = "Student — Billings", description = "Paiement étudiant après approbation d'inscription")
public class StudentBillingController {

    private final BillingRepository billingRepository;
    private final StudentRepository studentRepository;
    private final BillingMapper billingMapper;
    private final StripeService stripeService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mes paiements", description = "Retourne tous les billings de l'étudiant connecté")
    public ResponseEntity<List<BillingResponse>> getMyBillings(@AuthenticationPrincipal User user) {
        Student student = getStudent(user);
        List<BillingResponse> billings = billingRepository.findAllByStudentId(student.getId())
                .stream().map(billingMapper::toResponse).toList();
        return ResponseEntity.ok(billings);
    }

    @PostMapping("/{billingId}/select-method")
    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    @Operation(summary = "Choisir la méthode de paiement",
            description = "L'étudiant choisit CASH, BANK_TRANSFER ou STRIPE. Pour STRIPE, retourne l'URL de checkout.")
    public ResponseEntity<PaymentResponse> selectPaymentMethod(
            @AuthenticationPrincipal User user,
            @PathVariable String billingId,
            @Valid @RequestBody SelectPaymentMethodRequest request) {

        Student student = getStudent(user);

        Billing billing = billingRepository.findById(billingId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", billingId));

        // Verify ownership
        if (!billing.getStudent().getId().equals(student.getId())) {
            throw new IllegalStateException("Ce paiement ne vous appartient pas");
        }

        // Only PENDING billings can have method selected
        if (billing.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Ce paiement n'est plus en attente. Statut actuel : " + billing.getStatus());
        }

        // Verify enrollment is APPROVED
        if (billing.getEnrollment() == null || billing.getEnrollment().getStatus() != EnrollmentStatus.APPROVED) {
            throw new IllegalStateException("L'inscription associée n'est pas encore approuvée");
        }

        PaymentMethod method = request.getPaymentMethod();
        billing.setPaymentMethod(method);

        if (method == PaymentMethod.STRIPE) {
            return handleStripePayment(billing);
        }

        // For CASH / BANK_TRANSFER — just save the method, student pays offline
        billingRepository.save(billing);
        BillingResponse billingResponse = billingMapper.toResponse(billing);
        return ResponseEntity.ok(new PaymentResponse(billingResponse, null));
    }

    private ResponseEntity<PaymentResponse> handleStripePayment(Billing billing) {
        try {
            Session checkoutSession = stripeService.createCheckoutSession(
                    billing.getEnrollment().getCourse(), billing);

            billing.setStripeCheckoutSessionId(checkoutSession.getId());
            billingRepository.save(billing);

            BillingResponse billingResponse = billingMapper.toResponse(billing);
            return ResponseEntity.ok(new PaymentResponse(billingResponse, checkoutSession.getUrl()));
        } catch (Exception e) {
            log.error("Erreur lors de la création de la session Stripe pour billing {}", billing.getId(), e);
            throw new RuntimeException("Impossible de créer la session de paiement : " + e.getMessage());
        }
    }

    private Student getStudent(User user) {
        return studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + user.getId()));
    }
}
