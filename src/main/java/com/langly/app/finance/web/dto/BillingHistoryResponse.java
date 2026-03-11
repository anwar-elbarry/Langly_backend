package com.langly.app.finance.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BillingHistoryResponse {
    private String id;
    private BigDecimal price;
    private String status;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private String billingId;
}
