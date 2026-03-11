package com.langly.app.course.service;

import com.langly.app.course.web.dto.TeacherOverviewResponse;

public interface TeacherService {
    TeacherOverviewResponse getOverview(String teacherId);
}
