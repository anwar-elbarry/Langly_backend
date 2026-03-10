package com.langly.app.course.web.dto;

import com.langly.app.course.entity.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * US07 : Requête pour changer le statut d'une inscription (PASSED, FAILED,
 * etc.).
 */
@Data
public class UpdateEnrollmentStatusRequest {
    @NotNull
    private EnrollmentStatus status;
}
