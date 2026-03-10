package com.langly.app.course.service;

import com.langly.app.course.entity.Course;
import com.langly.app.course.entity.Enrollment;
import com.langly.app.course.entity.enums.EnrollmentStatus;
import com.langly.app.course.repository.CourseRepository;
import com.langly.app.course.repository.EnrollmentRepository;
import com.langly.app.course.web.dto.EnrollmentRequest;
import com.langly.app.course.web.dto.EnrollmentResponse;
import com.langly.app.course.web.mapper.EnrollmentMapper;
import com.langly.app.exception.AlreadyExistsException;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.student.entity.Student;
import com.langly.app.student.repository.StudentRepository;
import com.langly.app.student.service.CertificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final CertificationService certificationService;

    @Override
    @Transactional
    public EnrollmentResponse enroll(EnrollmentRequest request) {
        // Vérifier doublon
        if (enrollmentRepository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())) {
            throw new AlreadyExistsException("L'étudiant est déjà inscrit à ce cours");
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", request.getCourseId()));

        // Vérifier la capacité du cours
        long currentCount = enrollmentRepository.findAllByCourseId(course.getId()).size();
        if (course.getCapacity() != null && currentCount >= course.getCapacity()) {
            throw new IllegalStateException(
                    "Le cours a atteint sa capacité maximale de " + course.getCapacity() + " étudiants");
        }

        // Mettre à jour le niveau de l'étudiant
        student.setLevel(request.getLevel());
        studentRepository.save(student);

        // Créer l'inscription
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        enrollment.setEnrolledAt(LocalDate.now());
        enrollment.setCertificateIssued(false);

        return enrollmentMapper.toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public EnrollmentResponse getById(String id) {
        return enrollmentMapper.toResponse(
                enrollmentRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Enrollment", id)));
    }

    @Override
    public List<EnrollmentResponse> getAllByStudentId(String studentId) {
        return enrollmentRepository.findAllByStudentId(studentId)
                .stream().map(enrollmentMapper::toResponse).toList();
    }

    @Override
    public List<EnrollmentResponse> getAllByCourseId(String courseId) {
        return enrollmentRepository.findAllByCourseId(courseId)
                .stream().map(enrollmentMapper::toResponse).toList();
    }

    @Override
    public List<EnrollmentResponse> getAllBySchoolId(String schoolId) {
        return enrollmentRepository.findAllByStudentUserSchoolId(schoolId)
                .stream().map(enrollmentMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public EnrollmentResponse updateStatus(String enrollmentId, EnrollmentStatus status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));

        enrollment.setStatus(status);

        // Si WITHDRAWN ou FAILED → enregistrer la date de départ
        if (status == EnrollmentStatus.WITHDRAWN || status == EnrollmentStatus.FAILED) {
            enrollment.setLeftAt(LocalDate.now());
        }

        Enrollment saved = enrollmentRepository.save(enrollment);

        // US07 : Auto-générer le certificat si PASSED
        if (status == EnrollmentStatus.PASSED && !Boolean.TRUE.equals(enrollment.getCertificateIssued())) {
            try {
                certificationService.generateCertificate(enrollmentId);
                log.info("Certificat auto-généré pour enrollment {}", enrollmentId);
            } catch (Exception e) {
                log.error("Erreur lors de la génération auto du certificat pour enrollment {}", enrollmentId, e);
            }
        }

        return enrollmentMapper.toResponse(saved);
    }
}
