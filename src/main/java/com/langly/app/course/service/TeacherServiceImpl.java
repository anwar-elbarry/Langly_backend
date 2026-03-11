package com.langly.app.course.service;

import com.langly.app.course.entity.Course;
import com.langly.app.course.entity.Enrollment;
import com.langly.app.course.entity.enums.EnrollmentStatus;
import com.langly.app.course.repository.CourseRepository;
import com.langly.app.course.repository.EnrollmentRepository;
import com.langly.app.course.repository.SessionRepository;
import com.langly.app.course.web.dto.TeacherOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeacherServiceImpl implements TeacherService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SessionRepository sessionRepository;

    @Override
    public TeacherOverviewResponse getOverview(String teacherId) {
        List<Course> courses = courseRepository.findAllByTeacherId(teacherId);

        if (courses.isEmpty()) {
            return new TeacherOverviewResponse(0, 0, 0, 0, 0);
        }

        List<String> courseIds = courses.stream().map(Course::getId).toList();

        int totalCourses = courses.size();
        int activeCourses = (int) courses.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .count();

        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseIdIn(courseIds);

        int totalStudents = (int) enrollments.stream()
                .map(e -> e.getStudent().getId())
                .distinct()
                .count();

        long upcomingSessions = sessionRepository.countByCourseIdInAndScheduledAtAfter(
                courseIds, LocalDateTime.now());

        int pendingGrading = (int) enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.IN_PROGRESS)
                .count();

        return new TeacherOverviewResponse(
                totalCourses,
                activeCourses,
                totalStudents,
                (int) upcomingSessions,
                pendingGrading
        );
    }
}
