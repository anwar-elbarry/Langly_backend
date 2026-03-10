package com.langly.app.course.repository;

import com.langly.app.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findAllByTeacherSchoolId(String schoolId);
    boolean existsByCode(String code);

    /**
     * US03 : Cours actifs auxquels un étudiant est inscrit (enrollment IN_PROGRESS).
     */
    @Query("""
            SELECT c FROM Course c
            JOIN Enrollment e ON e.course = c
            WHERE e.student.id = :studentId
              AND e.status = com.langly.app.course.entity.enums.EnrollmentStatus.IN_PROGRESS
              AND c.isActive = true
            """)
    List<Course> findActiveByStudentId(String studentId);

    /**
     * Cours assignés à un professeur.
     */
    List<Course> findAllByTeacherId(String teacherId);
}
