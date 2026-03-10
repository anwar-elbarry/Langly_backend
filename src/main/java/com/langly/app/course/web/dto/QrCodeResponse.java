package com.langly.app.course.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * US05 : Réponse après génération du QR code pour une session.
 */
@Data
@AllArgsConstructor
public class QrCodeResponse {
    private String qrToken;
    private LocalDateTime expiresAt;
}
