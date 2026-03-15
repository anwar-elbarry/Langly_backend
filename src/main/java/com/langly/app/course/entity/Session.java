package com.langly.app.course.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.langly.app.course.entity.enums.Mode;

import java.time.LocalDateTime;
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
    private Integer durationMinutes;

    @Column(name = "schedualed_at")
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.ORDINAL)
    private Mode mode;

    /** Salle physique (pour IN_PERSON ou HYBRID) */
    private String room;

    /** Lien de réunion en ligne : Zoom, Google Meet, Teams, etc. (pour ONLINE ou HYBRID) */
    private String meetingLink;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Attendance> attendanceRecords = new ArrayList<>();
}
