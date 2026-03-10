package com.langly.app.finance.web.dto;

import com.langly.app.finance.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectPaymentMethodRequest {

    @NotNull(message = "La méthode de paiement est obligatoire")
    private PaymentMethod paymentMethod;
}
