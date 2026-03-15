package com.langly.app.course.repository;

import com.langly.app.course.entity.Attendance;
import com.langly.app.course.entity.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    List<Attendance> findAllBySessionId(String sessionId);

    boolean existsByStudentIdAndSessionId(String studentId, String sessionId);

    Optional<Attendance> findByStudentIdAndSessionId(String studentId, String sessionId);

    List<Attendance> findAllByStudentId(String studentId);

    List<Attendance> findAllByStudentIdAndSessionCourseId(String studentId, String courseId);

    long countBySessionIdAndStatus(String sessionId, AttendanceStatus status);

    long countByStudentIdAndStatus(String studentId, AttendanceStatus status);

    long countByStudentIdAndSessionCourseIdAndStatus(String studentId, String courseId, AttendanceStatus status);
}
