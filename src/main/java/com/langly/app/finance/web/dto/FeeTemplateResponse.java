package com.langly.app.finance.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FeeTemplateResponse {

    private String id;
    private String name;
    private String type;
    private BigDecimal amount;
    private Boolean isRecurring;
    private Boolean isActive;
}
