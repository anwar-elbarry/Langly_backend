package com.langly.app.course.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * US06 : Réponse pour un matériel de cours.
 */
@Data
public class CourseMaterialResponse {
    private String id;
    private String name;
    private String type;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    private String courseId;
}
