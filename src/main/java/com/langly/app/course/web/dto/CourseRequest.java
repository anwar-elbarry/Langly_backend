package com.langly.app.course.web.dto;

import com.langly.app.course.entity.enums.Level;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CourseRequest {
    @NotBlank private String name;
    @NotBlank private String code;
    @NotBlank private String language;
    @NotNull private Level requiredLevel;
    @NotNull private Level targetLevel;
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    @NotNull @DecimalMin(value = "0.0", inclusive = false) private BigDecimal price;
    @NotNull @Min(1) private Integer capacity;
    @NotNull @Min(1) private Integer sessionPerWeek;
    @Min(15) private Integer minutesPerSession;
    @NotNull private String teacherId;
    @NotNull private String schoolId;
}