package com.langly.app.course.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.langly.app.course.entity.enums.Mode;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "sessions")
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String description;
    private String durationMinutes;
    private String schedualedAt;
    private Mode mode;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "session",cascade = CascadeType.ALL)
    private List<Attendance> attendanceRecordes = new ArrayList<>();
}
