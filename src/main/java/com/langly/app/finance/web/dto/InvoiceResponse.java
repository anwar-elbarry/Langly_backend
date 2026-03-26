package com.langly.app.finance.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class InvoiceResponse {

    private String id;
    private String invoiceNumber;
    private String studentId;
    private String studentFullName;
    private String schoolId;
    private String enrollmentId;
    private String courseId;
    private String courseName;
    private BigDecimal total;
    private String status;
    private LocalDateTime issuedAt;
    private LocalDate dueDate;
    private List<InvoiceLineResponse> lines;
    private List<PaymentScheduleResponse> schedules;
}
