package com.langly.app.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.langly.app.course.entity.enums.Level;

@Data

@Entity
@Table(name = "certifications")

@NoArgsConstructor
@AllArgsConstructor
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Level level;
    private String language;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
