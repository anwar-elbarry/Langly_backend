package com.langly.app.finance.web.dto;

import com.langly.app.finance.entity.enums.BillingCycle;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SubscriptionUpdateRequest {

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String currency;

    private BillingCycle billingCycle;
}
