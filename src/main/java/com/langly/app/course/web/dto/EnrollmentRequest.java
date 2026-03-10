package com.langly.app.course.web.dto;

import com.langly.app.course.entity.enums.Level;
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

    /**
     * Niveau de l'étudiant au moment de l'inscription.
     * Met à jour la propriété {@code level} sur l'entité {@code Student}.
     */
    @NotNull(message = "Le niveau est obligatoire")
    private Level level;
}
