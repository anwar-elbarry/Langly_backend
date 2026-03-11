package com.langly.app.finance.web.dto;

import com.langly.app.finance.entity.enums.InstallmentPlan;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BillingSettingRequest {

    @NotNull(message = "Le taux de TVA est obligatoire")
    @DecimalMin(value = "0.0", message = "Le taux de TVA ne peut pas être négatif")
    private BigDecimal tvaRate;

    @NotNull(message = "Le délai d'échéance est obligatoire")
    private Integer dueDateDays;

    @NotNull(message = "Le plan d'échelonnement par défaut est obligatoire")
    private InstallmentPlan defaultInstallmentPlan;

    @NotNull(message = "Le blocage sur impayé est obligatoire")
    private Boolean blockOnUnpaid;

    @NotNull(message = "L'activation des réductions est obligatoire")
    private Boolean discountEnabled;
}
