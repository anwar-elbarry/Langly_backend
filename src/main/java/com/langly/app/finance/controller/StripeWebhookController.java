package com.langly.app.finance.controller;

import com.langly.app.course.entity.enums.EnrollmentStatus;
import com.langly.app.course.repository.EnrollmentRepository;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.entity.Billing;
import com.langly.app.finance.entity.BillingHistory;
import com.langly.app.finance.entity.enums.PaymentMethod;
import com.langly.app.finance.entity.enums.PaymentStatus;
import com.langly.app.finance.repository.BillingRepository;
import com.langly.app.finance.service.StripeService;
import com.langly.app.notification.entity.enums.NotificationType;
import com.langly.app.notification.service.NotificationService;
import com.langly.app.user.entity.User;
import com.langly.app.user.repository.UserRepository;
import com.langly.app.course.entity.enums.SchoolStatus;
import com.langly.app.finance.repository.SubscriptionRepository;
import com.langly.app.finance.repository.SubscriptionHistoryRepository;
import com.langly.app.school.repository.SchoolRepository;
import com.langly.app.finance.entity.Subscription;
import com.langly.app.finance.entity.SubscriptionHistory;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Hidden // Ne pas afficher dans Swagger
public class StripeWebhookController {

    private final BillingRepository billingRepository;
    private final StripeService stripeService;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final SchoolRepository schoolRepository;

    @PostMapping("/stripe")
    @Transactional
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = stripeService.verifyWebhookSignature(payload, sigHeader);
        } catch (SignatureVerificationException e) {
            log.warn("Signature Stripe invalide", e);
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject().orElse(null);

            if (session != null) {
                String billingId = session.getMetadata().get("billing_id");
                if (billingId != null) {
                    handlePaymentSuccess(billingId, session.getPaymentIntent());
                }

                String subscriptionId = session.getMetadata().get("subscription_id");
                if (subscriptionId != null) {
                    handleSubscriptionPaymentSuccess(subscriptionId);
                }
            }
        }

        return ResponseEntity.ok("OK");
    }

    private void handlePaymentSuccess(String billingId, String paymentIntentId) {
        Billing billing = billingRepository.findById(billingId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing", billingId));

        if (billing.getStatus() != PaymentStatus.PENDING) {
            log.info("Billing {} déjà confirmé, ignoré", billingId);
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        billing.setStatus(PaymentStatus.PAID);
        billing.setPaymentMethod(PaymentMethod.STRIPE);
        billing.setPaidAt(now);
        billing.setStripePaymentIntentId(paymentIntentId);

        // Historique
        BillingHistory history = new BillingHistory();
        history.setPrice(billing.getPrice());
        history.setStatus(PaymentStatus.PAID);
        history.setPaymentMethod(PaymentMethod.STRIPE);
        history.setPaidAt(now);
        history.setBilling(billing);
        billing.getHistories().add(history);

        billingRepository.save(billing);

        // Transition enrollment to IN_PROGRESS
        if (billing.getEnrollment() != null
                && billing.getEnrollment().getStatus() == EnrollmentStatus.APPROVED) {
            billing.getEnrollment().setStatus(EnrollmentStatus.IN_PROGRESS);
            enrollmentRepository.save(billing.getEnrollment());
        }

        // Notify school admin(s) about successful payment
        if (billing.getStudent() != null && billing.getStudent().getUser() != null
                && billing.getStudent().getUser().getSchool() != null) {
            String schoolId = billing.getStudent().getUser().getSchool().getId();
            List<User> schoolAdmins = userRepository.findAllBySchoolIdAndRoleName(schoolId, "SCHOOL_ADMIN");

            String studentName = billing.getStudent().getUser().getFirstName() + " "
                    + billing.getStudent().getUser().getLastName();
            String courseName = billing.getEnrollment() != null && billing.getEnrollment().getCourse() != null
                    ? billing.getEnrollment().getCourse().getName() : "N/A";

            for (User admin : schoolAdmins) {
                notificationService.sendNotification(
                        admin.getId(),
                        "Paiement en ligne reçu",
                        String.format("L'étudiant %s a payé le cours %s (%s MAD) via Stripe.",
                                studentName, courseName, billing.getPrice()),
                        NotificationType.PAYMENT_SUCCESS,
                        billing.getId(),
                        "BILLING"
                );
            }
        }

        log.info("Paiement Stripe confirmé pour billing {}", billingId);
    }

    private void handleSubscriptionPaymentSuccess(String subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElse(null);
        if (subscription == null || subscription.getStatus() == PaymentStatus.PAID) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        subscription.setStatus(PaymentStatus.PAID);

        SubscriptionHistory history = new SubscriptionHistory();
        history.setAmount(subscription.getAmount());
        history.setStatusAtThatTime(PaymentStatus.PAID);
        history.setPaymentMethod(PaymentMethod.STRIPE);
        history.setPaidAt(now);
        history.setSubscription(subscription);
        subscriptionHistoryRepository.save(history);

        com.langly.app.school.entity.School school = subscription.getSchool();
        if (school != null) {
            school.setStatus(SchoolStatus.ACTIVE);
            
            java.time.LocalDate newPeriodStart = subscription.getCurrentPeriodEnd() != null ? subscription.getCurrentPeriodEnd() : java.time.LocalDate.now();
            subscription.setCurrentPeriodStart(newPeriodStart);
            
            java.time.LocalDate newPeriodEnd = switch(subscription.getBillingCycle()) {
                case MONTHLY -> newPeriodStart.plusMonths(1);
                case YEARLY -> newPeriodStart.plusYears(1);
            };
            subscription.setCurrentPeriodEnd(newPeriodEnd);
            subscription.setNextPaymentDate(newPeriodEnd);
            
            schoolRepository.save(school);
        }

        subscriptionRepository.save(subscription);
        log.info("Paiement Stripe confirmé pour subscription {}", subscriptionId);

        // Notify school admins
        if (school != null) {
            String msg = "Votre paiement a été validé via Stripe. Votre école est maintenant active.";
            List<User> schoolAdmins = userRepository.findAllBySchoolIdAndRoleName(school.getId(), "SCHOOL_ADMIN");
            for (User admin : schoolAdmins) {
                notificationService.sendNotification(
                        admin.getId(),
                        "Abonnement Activé",
                        msg,
                        NotificationType.SUBSCRIPTION_ACTIVATED,
                        subscription.getId(),
                        "SUBSCRIPTION"
                );
            }
        }
    }
}
