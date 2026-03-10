package com.langly.app.course.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * US05 : Réponse pour un enregistrement de présence.
 */
@Data
public class AttendanceResponse {
    private String id;
    private String studentId;
    private String studentFullName;
    private String status;
    private LocalDateTime markedAt;
    private String sessionId;
}
