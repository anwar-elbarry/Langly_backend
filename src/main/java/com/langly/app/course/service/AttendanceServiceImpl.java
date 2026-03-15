package com.langly.app.course.service;

import com.langly.app.course.entity.Attendance;
import com.langly.app.course.entity.Course;
import com.langly.app.course.entity.Enrollment;
import com.langly.app.course.entity.Session;
import com.langly.app.course.entity.enums.AttendanceStatus;
import com.langly.app.course.repository.AttendanceRepository;
import com.langly.app.course.repository.EnrollmentRepository;
import com.langly.app.course.repository.SessionRepository;
import com.langly.app.course.web.dto.*;
import com.langly.app.course.web.mapper.AttendanceMapper;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.student.entity.Student;
import com.langly.app.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    public List<AttendanceResponse> getAttendanceBySession(String sessionId) {
        return attendanceRepository.findAllBySessionId(sessionId)
                .stream().map(attendanceMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public AttendanceResponse markManual(String sessionId, String studentId, AttendanceStatus status) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        String courseId = session.getCourse().getId();
        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new IllegalStateException("L'étudiant n'est pas inscrit au cours de cette session");
        }

        Optional<Attendance> existing = attendanceRepository.findByStudentIdAndSessionId(studentId, sessionId);
        Attendance attendance;
        if (existing.isPresent()) {
            attendance = existing.get();
            attendance.setStatus(status);
            attendance.setMarkedAt(LocalDateTime.now());
        } else {
            attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setSession(session);
            attendance.setStatus(status);
            attendance.setMarkedAt(LocalDateTime.now());
        }

        return attendanceMapper.toResponse(attendanceRepository.save(attendance));
    }

    @Override
    public List<AttendanceResponse> getFullAttendanceBySession(String sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId));

        String courseId = session.getCourse().getId();
        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId);
        Map<String, Attendance> attendanceMap = attendanceRepository.findAllBySessionId(sessionId)
                .stream().collect(Collectors.toMap(a -> a.getStudent().getId(), a -> a));

        List<AttendanceResponse> result = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            Student student = enrollment.getStudent();
            Attendance existing = attendanceMap.get(student.getId());
            if (existing != null) {
                result.add(attendanceMapper.toResponse(existing));
            } else {
                AttendanceResponse dto = new AttendanceResponse();
                dto.setId(null);
                dto.setStudentId(student.getId());
                dto.setStudentFullName(student.getUser().getFirstName() + " " + student.getUser().getLastName());
                dto.setStatus("UNMARKED");
                dto.setMarkedAt(null);
                dto.setSessionId(sessionId);
                result.add(dto);
            }
        }
        return result;
    }

    @Override
    public StudentAttendanceSummaryResponse getStudentGlobalSummary(String userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + userId));

        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentId(student.getId());
        List<String> courseIds = enrollments.stream().map(e -> e.getCourse().getId()).toList();

        long totalSessions = 0;
        for (String courseId : courseIds) {
            totalSessions += sessionRepository.findAllByCourseId(courseId).size();
        }

        long present = attendanceRepository.countByStudentIdAndStatus(student.getId(), AttendanceStatus.PRESENT);
        long absent = attendanceRepository.countByStudentIdAndStatus(student.getId(), AttendanceStatus.ABSENT);
        long late = attendanceRepository.countByStudentIdAndStatus(student.getId(), AttendanceStatus.LATE);
        long excused = attendanceRepository.countByStudentIdAndStatus(student.getId(), AttendanceStatus.EXCUSED);
        long marked = present + absent + late + excused;
        long unmarked = totalSessions - marked;

        return new StudentAttendanceSummaryResponse(totalSessions, present, absent, late, excused, Math.max(0, unmarked));
    }

    @Override
    public List<CourseAttendanceSummaryResponse> getStudentCourseSummaries(String userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + userId));

        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentId(student.getId());
        List<CourseAttendanceSummaryResponse> summaries = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            long totalSessions = sessionRepository.findAllByCourseId(course.getId()).size();
            long present = attendanceRepository.countByStudentIdAndSessionCourseIdAndStatus(student.getId(), course.getId(), AttendanceStatus.PRESENT);
            long absent = attendanceRepository.countByStudentIdAndSessionCourseIdAndStatus(student.getId(), course.getId(), AttendanceStatus.ABSENT);
            long late = attendanceRepository.countByStudentIdAndSessionCourseIdAndStatus(student.getId(), course.getId(), AttendanceStatus.LATE);
            long excused = attendanceRepository.countByStudentIdAndSessionCourseIdAndStatus(student.getId(), course.getId(), AttendanceStatus.EXCUSED);
            long marked = present + absent + late + excused;
            long unmarked = totalSessions - marked;

            summaries.add(new CourseAttendanceSummaryResponse(
                    course.getId(), course.getName(), totalSessions,
                    present, absent, late, excused, Math.max(0, unmarked)));
        }
        return summaries;
    }
}
