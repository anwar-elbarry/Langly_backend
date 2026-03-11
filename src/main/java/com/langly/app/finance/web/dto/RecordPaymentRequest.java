package com.langly.app.finance.web.dto;

import com.langly.app.finance.entity.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecordPaymentRequest {

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal amount;

    @NotNull(message = "La méthode de paiement est obligatoire")
    private PaymentMethod paymentMethod;
}
