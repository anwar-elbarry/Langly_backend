package com.langly.app.course.entity;

import com.langly.app.course.entity.enums.MaterialType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "course_materials")
@NoArgsConstructor
@AllArgsConstructor
public class CourseMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Enumerated(EnumType.ORDINAL)
    private MaterialType type;

    private String fileUrl;
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
