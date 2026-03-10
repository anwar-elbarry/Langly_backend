package com.langly.app.student.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * US07 : Réponse pour un certificat.
 */
@Data
public class CertificationResponse {
    private String id;
    private String language;
    private String level;
    private LocalDateTime issuedAt;
    private String pdfUrl;
    private String courseName;
    private String schoolName;
}
