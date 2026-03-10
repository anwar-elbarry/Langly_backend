package com.langly.app.course.entity;

import com.langly.app.student.entity.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.langly.app.course.entity.enums.EnrollmentStatus;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "enrollment")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;
    private LocalDate enrolledAt;
    private LocalDate leftAt;
    private Boolean certificateIssued;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
}
