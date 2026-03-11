package com.langly.app.finance.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceLineResponse {

    private String id;
    private String description;
    private BigDecimal amount;
    private String feeTemplateId;
}
