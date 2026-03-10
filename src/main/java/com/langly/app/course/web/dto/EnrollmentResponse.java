package com.langly.app.course.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EnrollmentResponse {

    private String id;
    private String status;
    private LocalDate enrolledAt;
    private LocalDate leftAt;
    private Boolean certificateIssued;

    private String studentId;
    private String studentFullName;

    private String courseId;
    private String courseName;
    private String level;
    private BigDecimal coursePrice;
}
