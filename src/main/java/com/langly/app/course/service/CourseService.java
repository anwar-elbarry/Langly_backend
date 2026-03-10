package com.langly.app.course.service;

import com.langly.app.course.web.dto.ActiveCourseResponse;
import com.langly.app.course.web.dto.CourseRequest;
import com.langly.app.course.web.dto.CourseResponse;

import java.util.List;

public interface CourseService {
    CourseResponse create(CourseRequest request);
    CourseResponse getById(String id);
    List<CourseResponse> getAllBySchoolId(String schoolId);
    CourseResponse update(String id, CourseRequest request);
    void delete(String id);

    /** US03 : Cours actifs de l'étudiant avec prochaines sessions */
    List<ActiveCourseResponse> getActiveCoursesForStudent(String studentId);

    /** Cours assignés à un professeur */
    List<CourseResponse> getAllByTeacherId(String teacherId);
}
