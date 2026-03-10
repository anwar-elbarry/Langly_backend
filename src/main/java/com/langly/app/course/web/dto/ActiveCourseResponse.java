package com.langly.app.course.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * US03 : DTO retourné à l'étudiant pour ses cours actifs.
 * Contient les infos de la carte cours : titre, prof, prochaines sessions, salle/meeting link.
 */
@Getter
@Setter
public class ActiveCourseResponse {
    private String id;
    private String name;
    private String code;
    private String language;
    private String requiredLevel;
    private String targetLevel;
    private BigDecimal price;
    private Boolean isActive;

    /** Nom complet du professeur */
    private String teacherFullName;

    /** Prochaines sessions à venir */
    private List<SessionResponse> upcomingSessions;
}
