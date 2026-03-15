package com.langly.app.course.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SessionResponse {
    private String id;
    private String title;
    private String description;
    private Integer durationMinutes;
    private LocalDateTime scheduledAt;
    private String mode;
    private String room;
    private String meetingLink;
    private String courseId;
    private String courseName;
    private int presentCount;
    private int totalEnrolled;
}
