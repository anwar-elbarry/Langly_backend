package com.langly.app.finance.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BillingSettingResponse {

    private String id;
    private String schoolId;
    private BigDecimal tvaRate;
    private Integer dueDateDays;
    private String defaultInstallmentPlan;
    private Boolean blockOnUnpaid;
    private Boolean discountEnabled;
}
