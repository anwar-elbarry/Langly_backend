package com.langly.app.course.repository;

import com.langly.app.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findAllByTeacherSchoolId(String schoolId);
    boolean existsByCode(String code);
}
