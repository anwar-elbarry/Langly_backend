package com.langly.app.course.repository;

import com.langly.app.course.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

    List<Session> findAllByCourseIdOrderByScheduledAtAsc(String courseId);

    List<Session> findAllByCourseIdAndScheduledAtAfterOrderByScheduledAtAsc(String courseId, LocalDateTime after);

    List<Session> findAllByCourseId(String courseId);
}
