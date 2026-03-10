package com.langly.app.course.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentEnrollmentRequest {

    @NotBlank(message = "L'identifiant du cours est obligatoire")
    private String courseId;
}
