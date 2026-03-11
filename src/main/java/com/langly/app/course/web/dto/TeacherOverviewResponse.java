package com.langly.app.course.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeacherOverviewResponse {
    private int totalCourses;
    private int activeCourses;
    private int totalStudents;
    private int upcomingSessions;
    private int pendingGrading;
}
