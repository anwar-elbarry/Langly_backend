package com.langly.app.finance.service;

import com.langly.app.course.entity.Course;
import com.langly.app.finance.entity.Billing;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * US04 : Service d'intégration Stripe (Checkout Session + Webhook).
 */
@Slf4j
@Service
public class StripeService {

    @Value("${stripe.api-key}")
    private String apiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    /**
     * Crée une session Stripe Checkout pour le paiement d'un cours.
     */
    public Session createCheckoutSession(Course course, Billing billing) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(
                        frontendBaseUrl + "/student/enroll?payment=success&session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendBaseUrl + "/student/enroll?payment=cancelled")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("mad")
                                                .setUnitAmount(course.getPrice()
                                                        .multiply(java.math.BigDecimal.valueOf(100)).longValue())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(course.getName() + " (" + course.getCode()
                                                                        + ")")
                                                                .setDescription(
                                                                        "Inscription au cours " + course.getName())
                                                                .build())
                                                .build())
                                .build())
                .putMetadata("billing_id", billing.getId())
                .build();

        return Session.create(params);
    }

    /**
     * Vérifie la signature du webhook Stripe et retourne l'Event.
     */
    public Event verifyWebhookSignature(String payload, String sigHeader) throws SignatureVerificationException {
        return Webhook.constructEvent(payload, sigHeader, webhookSecret);
    }

    /**
     * Crée une session Stripe Checkout pour le paiement d'un abonnement.
     */
    public Session createSubscriptionCheckoutSession(com.langly.app.finance.entity.Subscription subscription) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(
                        frontendBaseUrl + "/admin/subscription?payment=success&session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendBaseUrl + "/admin/subscription?payment=cancelled")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(subscription.getCurrency() != null ? subscription.getCurrency().toLowerCase() : "mad")
                                                .setUnitAmount(subscription.getAmount()
                                                        .multiply(java.math.BigDecimal.valueOf(100)).longValue())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Abonnement SaaS")
                                                                .setDescription("Abonnement " + subscription.getBillingCycle().name())
                                                                .build())
                                                .build())
                                .build())
                .putMetadata("subscription_id", subscription.getId())
                .build();

        return Session.create(params);
    }
}
