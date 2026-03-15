package com.langly.app.course.web.dto;

import com.langly.app.course.entity.enums.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ManualAttendanceRequest {
    @NotBlank
    private String studentId;

    @NotNull
    private AttendanceStatus status;
}
