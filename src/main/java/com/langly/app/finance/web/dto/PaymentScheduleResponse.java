package com.langly.app.finance.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentScheduleResponse {

    private String id;
    private Integer installment;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String status;
    private LocalDateTime paidAt;
}
