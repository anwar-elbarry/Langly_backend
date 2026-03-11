package com.langly.app.finance.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class DiscountResponse {

    private String id;
    private String name;
    private String type;
    private BigDecimal value;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
