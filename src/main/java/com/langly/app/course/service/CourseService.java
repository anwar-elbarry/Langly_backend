package com.langly.app.course.service;

import com.langly.app.course.web.dto.CourseRequest;
import com.langly.app.course.web.dto.CourseResponse;

import java.util.List;

public interface CourseService {
    CourseResponse create(CourseRequest request);
    CourseResponse getById(String id);
    List<CourseResponse> getAllBySchoolId(String schoolId);
    CourseResponse update(String id, CourseRequest request);
    void delete(String id);
}
