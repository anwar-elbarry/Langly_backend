package com.langly.app.course.service;

import com.langly.app.course.entity.Course;
import com.langly.app.course.entity.Session;
import com.langly.app.course.repository.CourseRepository;
import com.langly.app.course.repository.SessionRepository;
import com.langly.app.course.web.dto.SessionRequest;
import com.langly.app.course.web.dto.SessionResponse;
import com.langly.app.course.web.mapper.SessionMapper;
import com.langly.app.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final CourseRepository courseRepository;
    private final SessionMapper sessionMapper;

    @Override
    @Transactional
    public SessionResponse create(SessionRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", request.getCourseId()));

        validateSessionDate(request.getScheduledAt(), course);

        Session session = sessionMapper.toEntity(request);
        session.setCourse(course);

        return sessionMapper.toResponse(sessionRepository.save(session));
    }

    @Override
    public SessionResponse getById(String id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session", id));
        return sessionMapper.toResponse(session);
    }

    @Override
    public List<SessionResponse> getAllByCourseId(String courseId) {
        return sessionRepository.findAllByCourseIdOrderByScheduledAtAsc(courseId)
                .stream().map(sessionMapper::toResponse).toList();
    }

    @Override
    public List<SessionResponse> getUpcomingByCourseId(String courseId) {
        return sessionRepository.findAllByCourseIdAndScheduledAtAfterOrderByScheduledAtAsc(courseId, LocalDateTime.now())
                .stream().map(sessionMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public SessionResponse update(String id, SessionRequest request) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session", id));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", request.getCourseId()));

        validateSessionDate(request.getScheduledAt(), course);

        session.setTitle(request.getTitle());
        session.setDescription(request.getDescription());
        session.setDurationMinutes(request.getDurationMinutes());
        session.setScheduledAt(request.getScheduledAt());
        session.setMode(request.getMode());
        session.setRoom(request.getRoom());
        session.setMeetingLink(request.getMeetingLink());
        session.setCourse(course);

        return sessionMapper.toResponse(sessionRepository.save(session));
    }

    private void validateSessionDate(LocalDateTime scheduledAt, Course course) {
        LocalDate sessionDate = scheduledAt.toLocalDate();
        if (course.getStartDate() != null && sessionDate.isBefore(course.getStartDate())) {
            throw new IllegalArgumentException(
                    "La session ne peut pas être planifiée avant la date de début du cours (" + course.getStartDate() + ")");
        }
        if (course.getEndDate() != null && sessionDate.isAfter(course.getEndDate())) {
            throw new IllegalArgumentException(
                    "La session ne peut pas être planifiée après la date de fin du cours (" + course.getEndDate() + ")");
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session", id));
        sessionRepository.delete(session);
    }
}
