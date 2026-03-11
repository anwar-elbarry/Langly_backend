package com.langly.app.finance.web.dto;

import com.langly.app.finance.entity.enums.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DiscountRequest {

    @NotBlank(message = "Le nom de la réduction est obligatoire")
    private String name;

    @NotNull(message = "Le type de réduction est obligatoire")
    private DiscountType type;

    @NotNull(message = "La valeur est obligatoire")
    @DecimalMin(value = "0.01", message = "La valeur doit être supérieure à 0")
    private BigDecimal value;

    private Boolean isActive = true;
}
