package com.langly.app.finance.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FeePaymentResponse {

    private String id;
    private String feeTemplateId;
    private String feeTemplateName;
    private String studentId;
    private String studentFullName;
    private BigDecimal amount;
    private LocalDate paidAt;
    private String note;
    private Boolean isClosed;
}
