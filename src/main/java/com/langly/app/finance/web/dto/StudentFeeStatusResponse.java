package com.langly.app.finance.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeStatusResponse {

    private String feeTemplateId;
    private String feeTemplateName;
    private String feeType;
    private BigDecimal feeAmount;
    private Boolean isRecurring;
    private BigDecimal totalPaid;
    private Integer paymentCount;
    private Boolean isClosed;
    private String status; // "PAID", "UNPAID", "PARTIALLY_PAID"
}
