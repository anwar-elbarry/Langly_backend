package com.langly.app.course.repository;

import com.langly.app.course.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    List<Attendance> findAllBySessionId(String sessionId);

    boolean existsByStudentIdAndSessionId(String studentId, String sessionId);
}
