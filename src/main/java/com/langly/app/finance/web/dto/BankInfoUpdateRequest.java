package com.langly.app.finance.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BankInfoUpdateRequest {

    @NotBlank(message = "Bank name is required")
    @Size(max = 255)
    private String bankName;

    @NotBlank(message = "Account holder is required")
    @Size(max = 255)
    private String accountHolder;

    @NotBlank(message = "IBAN is required")
    @Size(max = 255)
    private String iban;

    @NotBlank(message = "Motive is required")
    @Size(max = 255)
    private String motive;

    @Size(max = 500)
    private String note;
}
