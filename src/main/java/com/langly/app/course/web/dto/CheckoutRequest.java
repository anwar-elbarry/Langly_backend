package com.langly.app.course.web.dto;

import com.langly.app.course.entity.enums.Level;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * US04 : Requête d'inscription avec paiement Stripe.
 */
@Data
public class CheckoutRequest {
    @NotBlank
    private String courseId;

    @NotNull
    private Level level;
}
