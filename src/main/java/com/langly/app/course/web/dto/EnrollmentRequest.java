package com.langly.app.course.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentRequest {

    @NotNull(message = "L'identifiant de l'étudiant est obligatoire")
    private String studentId;

    @NotNull(message = "L'identifiant du cours est obligatoire")
    private String courseId;
}
