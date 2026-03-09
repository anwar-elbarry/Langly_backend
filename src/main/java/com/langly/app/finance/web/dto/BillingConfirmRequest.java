package com.langly.app.finance.web.dto;

import com.langly.app.finance.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingConfirmRequest {

    /**
     * Méthode de paiement : uniquement CASH ou BANK_TRANSFER autorisés
     * pour une validation manuelle par l'admin.
     */
    @NotNull(message = "La méthode de paiement est obligatoire")
    private PaymentMethod paymentMethod;
}
