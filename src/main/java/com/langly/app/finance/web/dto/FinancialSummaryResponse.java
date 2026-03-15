package com.langly.app.finance.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class FinancialSummaryResponse {
    private BigDecimal totalRevenue;
    private BigDecimal totalTva;
    private BigDecimal totalTtc;
    private BigDecimal paidRevenue;
    private BigDecimal paidTva;
    private BigDecimal pendingRevenue;
    private BigDecimal pendingTva;
    private long invoiceCount;
    private long paidCount;
    private long unpaidCount;
    private BigDecimal tvaRate;
}
