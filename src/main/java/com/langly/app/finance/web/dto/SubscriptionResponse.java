package com.langly.app.finance.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SubscriptionResponse {
    private String id;
    private String schoolId;
    private String schoolName;
    private BigDecimal amount;
    private String currency;
    private String billingCycle;
    private LocalDate currentPeriodStart;
    private LocalDate currentPeriodEnd;
    private String status;
    private LocalDate nextPaymentDate;
}
