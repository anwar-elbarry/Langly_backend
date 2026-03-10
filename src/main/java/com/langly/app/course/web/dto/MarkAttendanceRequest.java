package com.langly.app.course.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * US05 : Requête pour marquer la présence via QR code.
 */
@Data
public class MarkAttendanceRequest {
    @NotBlank
    private String sessionId;

    @NotBlank
    private String qrToken;
}
