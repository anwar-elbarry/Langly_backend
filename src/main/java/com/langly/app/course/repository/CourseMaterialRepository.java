package com.langly.app.course.repository;

import com.langly.app.course.entity.CourseMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, String> {
    List<CourseMaterial> findAllByCourseId(String courseId);
}
