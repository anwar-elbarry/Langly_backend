package com.langly.app.course.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentAttendanceSummaryResponse {
    private long totalSessions;
    private long present;
    private long absent;
    private long late;
    private long excused;
    private long unmarked;
}
