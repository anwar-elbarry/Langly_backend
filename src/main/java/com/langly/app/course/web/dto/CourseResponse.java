package com.langly.app.course.web.dto;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
public class CourseResponse {
    private String id;
    private String name;
    private String code;
    private String language;
    private String requiredLevel;
    private String targetLevel;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private BigDecimal price;
    private Integer capacity;
    private Integer sessionPerWeek;
    private Integer minutesPerSession;
    private String teacherId;
    private String teacherFullName;
    private int enrolledCount;
}