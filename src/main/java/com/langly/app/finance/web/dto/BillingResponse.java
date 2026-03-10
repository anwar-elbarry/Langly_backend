package com.langly.app.finance.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BillingResponse {
    private String id;
    private BigDecimal price;
    private String status;
    private String paymentMethod;
    private LocalDate nextBillDate;
    private LocalDateTime paidAt;
    private String studentId;
    private String studentFullName;
    private String enrollmentId;
    private String courseId;
    private String courseName;
}
