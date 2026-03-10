package com.langly.app.course.service;

import com.langly.app.course.entity.Attendance;
import com.langly.app.course.entity.Session;
import com.langly.app.course.entity.enums.AttendanceStatus;
import com.langly.app.course.repository.AttendanceRepository;
import com.langly.app.course.repository.EnrollmentRepository;
import com.langly.app.course.repository.SessionRepository;
import com.langly.app.course.web.dto.AttendanceResponse;
import com.langly.app.course.web.dto.QrCodeResponse;
import com.langly.app.course.web.mapper.AttendanceMapper;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.student.entity.Student;
import com.langly.app.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    @Transactional
    public QrCodeResponse generateQr(String sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId));

        String qrToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        session.setQrToken(qrToken);
        session.setQrExpiresAt(expiresAt);
        sessionRepository.save(session);

        return new QrCodeResponse(qrToken, expiresAt);
    }

    @Override
    @Transactional
    public AttendanceResponse markPresent(String userId, String sessionId, String qrToken) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId));

        // Vérifier le QR token
        if (session.getQrToken() == null || !session.getQrToken().equals(qrToken)) {
            throw new IllegalArgumentException("QR code invalide");
        }

        // Vérifier l'expiration
        if (session.getQrExpiresAt() == null || LocalDateTime.now().isAfter(session.getQrExpiresAt())) {
            throw new IllegalStateException("Le QR code a expiré");
        }

        // Résoudre l'étudiant
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + userId));

        // Vérifier que l'étudiant est inscrit au cours de la session
        String courseId = session.getCourse().getId();
        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new IllegalStateException("Vous n'êtes pas inscrit au cours de cette session");
        }

        // Vérifier doublon
        if (attendanceRepository.existsByStudentIdAndSessionId(student.getId(), sessionId)) {
            throw new IllegalStateException("Votre présence est déjà enregistrée pour cette session");
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSession(session);
        attendance.setStatus(AttendanceStatus.PRESENT);
        attendance.setMarkedAt(LocalDateTime.now());

        return attendanceMapper.toResponse(attendanceRepository.save(attendance));
    }

    @Override
    public List<AttendanceResponse> getAttendanceBySession(String sessionId) {
        return attendanceRepository.findAllBySessionId(sessionId)
                .stream().map(attendanceMapper::toResponse).toList();
    }
}
