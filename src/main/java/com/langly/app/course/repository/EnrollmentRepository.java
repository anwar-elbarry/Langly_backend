package com.langly.app.course.repository;

import com.langly.app.course.entity.Enrollment;
import com.langly.app.course.entity.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
    List<Enrollment> findAllByStudentId(String studentId);
    List<Enrollment> findAllByCourseId(String courseId);
    List<Enrollment> findAllByStudentUserSchoolId(String schoolId);
    boolean existsByStudentIdAndCourseId(String studentId, String courseId);
    List<Enrollment> findAllByStudentUserSchoolIdAndStatus(String schoolId, EnrollmentStatus status);

    List<Enrollment> findAllByCourseIdIn(List<String> courseIds);
}
