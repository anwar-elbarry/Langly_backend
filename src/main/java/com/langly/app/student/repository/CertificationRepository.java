package com.langly.app.student.repository;

import com.langly.app.student.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, String> {
    List<Certification> findAllByStudentId(String studentId);
}
