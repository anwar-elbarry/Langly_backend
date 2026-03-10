package com.langly.app.course.entity;

import com.langly.app.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.langly.app.course.entity.enums.Level;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    @Column(unique = true)
    private String code;
    private String language;
    @Enumerated(EnumType.STRING)
    private Level requiredLevel;
    @Enumerated(EnumType.STRING)
    private Level targetLevel;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private BigDecimal price;
    private Integer capacity;
    private Integer sessionPerWeek;
    private Integer minutesPerSession;

    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;
}
