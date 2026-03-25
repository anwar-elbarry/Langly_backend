package com.langly.app.finance.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FeePaymentRequest {

    @NotBlank(message = "L'ID du modèle de frais est obligatoire")
    private String feeTemplateId;

    @NotBlank(message = "L'ID de l'étudiant est obligatoire")
    private String studentId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal amount;

    @NotNull(message = "La date de paiement est obligatoire")
    private LocalDate paidAt;

    private String note;
}
