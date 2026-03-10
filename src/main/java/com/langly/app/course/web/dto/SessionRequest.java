package com.langly.app.course.web.dto;

import com.langly.app.course.entity.enums.Mode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SessionRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String description;

    @NotNull(message = "La durée est obligatoire")
    @Min(value = 15, message = "La durée minimale est de 15 minutes")
    private Integer durationMinutes;

    @NotNull(message = "La date et l'heure sont obligatoires")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Le mode est obligatoire")
    private Mode mode;

    /** Salle physique (optionnel, pour IN_PERSON ou HYBRID) */
    private String room;

    /** Lien de réunion en ligne : Zoom, Google Meet, Teams, etc. (optionnel, pour ONLINE ou HYBRID) */
    private String meetingLink;

    @NotNull(message = "L'identifiant du cours est obligatoire")
    private String courseId;
}
