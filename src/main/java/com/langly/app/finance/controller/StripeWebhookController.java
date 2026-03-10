package com.langly.app.finance.controller;

import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.entity.Billing;
import com.langly.app.finance.entity.BillingHistory;
import com.langly.app.finance.entity.enums.PaymentMethod;
import com.langly.app.finance.entity.enums.PaymentStatus;
import com.langly.app.finance.repository.BillingRepository;
import com.langly.app.finance.service.StripeService;
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

/**
 * US04 : Webhook Stripe pour confirmer le paiement.
 * Endpoint public (pas d'auth JWT) — la sécurité est assurée par la signature
 * Stripe.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Hidden // Ne pas afficher dans Swagger
public class StripeWebhookController {

    private final BillingRepository billingRepository;
    private final StripeService stripeService;

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
        log.info("Paiement Stripe confirmé pour billing {}", billingId);
    }
}
