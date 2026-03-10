package com.langly.app.course.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * US04 : Réponse renvoyée après création du Stripe Checkout.
 */
@Data
@AllArgsConstructor
public class CheckoutResponse {
    private String checkoutUrl;
    private String billingId;
}
