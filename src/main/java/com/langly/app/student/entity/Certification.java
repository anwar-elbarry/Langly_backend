package com.langly.app.student.entity;

import com.langly.app.course.entity.Course;
import com.langly.app.school.entity.School;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.langly.app.course.entity.enums.Level;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "certifications")
@NoArgsConstructor
@AllArgsConstructor
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private Level level;
    private String language;

    /** Date de délivrance du certificat */
    private LocalDateTime issuedAt;

    /** URL du fichier PDF généré */
    private String pdfUrl;

    /** Signature digitale (texte ou chemin image) */
    private String digitalSignature;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;
}
