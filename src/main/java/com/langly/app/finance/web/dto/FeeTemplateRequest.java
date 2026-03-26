package com.langly.app.finance.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FeeTemplateRequest {

    @NotBlank(message = "Le nom du frais est obligatoire")
    private String name;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal amount;

    @NotNull(message = "Le champ récurrent est obligatoire")
    private Boolean isRecurring;

    private Boolean isActive = true;
}
