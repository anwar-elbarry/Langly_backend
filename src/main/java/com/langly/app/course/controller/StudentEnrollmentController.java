package com.langly.app.course.controller;

import com.langly.app.course.entity.Course;
import com.langly.app.course.entity.Enrollment;
import com.langly.app.course.entity.enums.EnrollmentStatus;
import com.langly.app.course.repository.CourseRepository;
import com.langly.app.course.repository.EnrollmentRepository;
import com.langly.app.course.web.dto.CheckoutRequest;
import com.langly.app.course.web.dto.CheckoutResponse;
import com.langly.app.exception.AlreadyExistsException;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.entity.Billing;
import com.langly.app.finance.entity.enums.PaymentStatus;
import com.langly.app.finance.repository.BillingRepository;
import com.langly.app.finance.service.StripeService;
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

import java.time.LocalDate;

/**
 * US04 : L'étudiant s'inscrit à un cours et paie via Stripe.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/student/enrollments")
@RequiredArgsConstructor
@Tag(name = "Student — Enrollments", description = "US04 : Inscription étudiant avec paiement Stripe")
public class StudentEnrollmentController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final BillingRepository billingRepository;
    private final StripeService stripeService;

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    @Operation(summary = "Initier le paiement Stripe", description = "Crée une inscription PENDING + Billing PENDING, puis retourne l'URL de Stripe Checkout")
    public ResponseEntity<CheckoutResponse> checkout(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CheckoutRequest request) {

        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + user.getId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", request.getCourseId()));

        // Vérifier doublon
        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new AlreadyExistsException("Vous êtes déjà inscrit à ce cours");
        }

        // Vérifier capacité
        long currentCount = enrollmentRepository.findAllByCourseId(course.getId()).size();
        if (course.getCapacity() != null && currentCount >= course.getCapacity()) {
            throw new IllegalStateException("Le cours a atteint sa capacité maximale");
        }

        // Mettre à jour le niveau
        student.setLevel(request.getLevel());
        studentRepository.save(student);

        // Créer l'inscription
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        enrollment.setEnrolledAt(LocalDate.now());
        enrollment.setCertificateIssued(false);
        enrollment = enrollmentRepository.save(enrollment);

        // Créer le billing PENDING
        Billing billing = new Billing();
        billing.setPrice(course.getPrice());
        billing.setStatus(PaymentStatus.PENDING);
        billing.setStudent(student);
        billing.setEnrollment(enrollment);
        billing = billingRepository.save(billing);

        // Créer la session Stripe
        try {
            Session checkoutSession = stripeService.createCheckoutSession(course, billing);

            // Sauvegarder les IDs Stripe
            billing.setStripeCheckoutSessionId(checkoutSession.getId());
            billingRepository.save(billing);

            return ResponseEntity.ok(new CheckoutResponse(checkoutSession.getUrl(), billing.getId()));
        } catch (Exception e) {
            log.error("Erreur lors de la création de la session Stripe", e);
            throw new RuntimeException("Impossible de créer la session de paiement : " + e.getMessage());
        }
    }

    @GetMapping("/{billingId}/invoice")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Télécharger la facture PDF", description = "Retourne l'URL de la facture PDF pour un billing donné")
    public ResponseEntity<String> getInvoice(
            @AuthenticationPrincipal User user,
            @PathVariable String billingId) {

        Billing billing = billingRepository.findById(billingId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", billingId));

        // Vérifier que le billing appartient bien à cet étudiant
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
