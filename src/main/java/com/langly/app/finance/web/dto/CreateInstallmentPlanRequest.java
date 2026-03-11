package com.langly.app.finance.web.dto;

import com.langly.app.finance.entity.enums.InstallmentPlan;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInstallmentPlanRequest {

    @NotNull(message = "Le plan d'échelonnement est obligatoire")
    private InstallmentPlan plan;
}
