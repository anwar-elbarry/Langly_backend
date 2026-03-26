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
import com.langly.app.finance.entity.Billing;
import com.langly.app.finance.entity.enums.PaymentStatus;
import com.langly.app.finance.repository.BillingRepository;
import com.langly.app.finance.service.InvoiceService;
import com.langly.app.notification.entity.enums.NotificationType;
import com.langly.app.notification.service.NotificationService;
import com.langly.app.student.entity.Student;
import com.langly.app.student.repository.StudentRepository;
import com.langly.app.user.entity.User;
import com.langly.app.user.repository.UserRepository;
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
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final InvoiceService invoiceService;
    private final BillingRepository billingRepository;

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

        // Validate level match
        if (student.getLevel() == null) {
            throw new IllegalStateException("Le niveau de l'étudiant n'est pas défini. Veuillez compléter le profil.");
        }
        if (student.getLevel() != course.getRequiredLevel()) {
            throw new IllegalStateException(
                    "Le niveau de l'étudiant (" + student.getLevel() + ") ne correspond pas au niveau requis du cours (" + course.getRequiredLevel() + ")");
        }

        // Vérifier la capacité du cours
        long currentCount = enrollmentRepository.findAllByCourseId(course.getId()).size();
        if (course.getCapacity() != null && currentCount >= course.getCapacity()) {
            throw new IllegalStateException(
                    "Le cours a atteint sa capacité maximale de " + course.getCapacity() + " étudiants");
        }

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
    @Transactional
    public EnrollmentResponse requestEnrollment(String studentId, String courseId) {
        // Vérifier doublon
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AlreadyExistsException("Vous êtes déjà inscrit à ce cours");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        // Validate level match
        if (student.getLevel() == null) {
            throw new IllegalStateException("Votre niveau n'est pas défini. Veuillez compléter votre profil.");
        }
        if (student.getLevel() != course.getRequiredLevel()) {
            throw new IllegalStateException(
                    "Votre niveau (" + student.getLevel() + ") ne correspond pas au niveau requis du cours (" + course.getRequiredLevel() + ")");
        }

        // Vérifier la capacité du cours
        long currentCount = enrollmentRepository.findAllByCourseId(course.getId()).size();
        if (course.getCapacity() != null && currentCount >= course.getCapacity()) {
            throw new IllegalStateException("Le cours a atteint sa capacité maximale");
        }

        // Créer l'inscription en PENDING_APPROVAL (pas de billing encore)
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.PENDING_APPROVAL);
        enrollment.setEnrolledAt(LocalDate.now());
        enrollment.setCertificateIssued(false);

        Enrollment saved = enrollmentRepository.save(enrollment);

        // Notify all SCHOOL_ADMINs of this student's school
        try {
            String schoolId = student.getUser().getSchool().getId();
            List<User> admins = userRepository.findAllBySchoolIdAndRoleName(schoolId, "SCHOOL_ADMIN");
            String studentName = student.getUser().getFirstName() + " " + student.getUser().getLastName();
            for (User admin : admins) {
                notificationService.sendNotification(
                        admin.getId(),
                        "Nouvelle demande d'inscription",
                        studentName + " demande à s'inscrire au cours " + course.getName(),
                        NotificationType.ENROLLMENT_REQUEST,
                        saved.getId(),
                        "ENROLLMENT"
                );
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification ENROLLMENT_REQUEST pour {}", saved.getId(), e);
        }

        return enrollmentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EnrollmentResponse approveEnrollment(String enrollmentId, List<String> discountIds) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));

        if (enrollment.getStatus() != EnrollmentStatus.PENDING_APPROVAL) {
            throw new IllegalStateException(
                    "Seules les inscriptions en attente d'approbation peuvent être approuvées. Statut actuel : " + enrollment.getStatus());
        }

        enrollment.setStatus(EnrollmentStatus.APPROVED);
        Enrollment saved = enrollmentRepository.save(enrollment);

        // Generate invoice for this enrollment
        var invoiceResponse = invoiceService.generateInvoice(enrollmentId, discountIds);

        // Create Billing record so student can select payment method
        Billing billing = new Billing();
        billing.setPrice(invoiceResponse.getTotal());
        billing.setStatus(PaymentStatus.PENDING);
        billing.setEnrollment(saved);
        billing.setStudent(enrollment.getStudent());
        billingRepository.save(billing);

        // Notify the student that their enrollment was approved
        try {
            String userId = enrollment.getStudent().getUser().getId();
            String courseName = enrollment.getCourse().getName();
            notificationService.sendNotification(
                    userId,
                    "Inscription approuvée",
                    "Votre inscription au cours " + courseName + " a été approuvée. Veuillez procéder au paiement.",
                    NotificationType.ENROLLMENT_APPROVED,
                    saved.getId(),
                    "ENROLLMENT"
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification ENROLLMENT_APPROVED pour {}", enrollmentId, e);
        }

        log.info("Enrollment {} approved. Invoice generated for student {}", enrollmentId, enrollment.getStudent().getId());

        return enrollmentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EnrollmentResponse rejectEnrollment(String enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));

        if (enrollment.getStatus() != EnrollmentStatus.PENDING_APPROVAL) {
            throw new IllegalStateException(
                    "Seules les inscriptions en attente d'approbation peuvent être rejetées. Statut actuel : " + enrollment.getStatus());
        }

        enrollment.setStatus(EnrollmentStatus.REJECTED);
        enrollment.setLeftAt(LocalDate.now());

        Enrollment saved = enrollmentRepository.save(enrollment);

        // Notify the student that their enrollment was rejected
        try {
            String userId = enrollment.getStudent().getUser().getId();
            String courseName = enrollment.getCourse().getName();
            notificationService.sendNotification(
                    userId,
                    "Inscription refusée",
                    "Votre demande d'inscription au cours " + courseName + " a été refusée.",
                    NotificationType.ENROLLMENT_REJECTED,
                    saved.getId(),
                    "ENROLLMENT"
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification ENROLLMENT_REJECTED pour {}", enrollmentId, e);
        }

        log.info("Enrollment {} rejected", enrollmentId);

        return enrollmentMapper.toResponse(saved);
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

        // Notify School Admin to generate certificate if PASSED
        if (status == EnrollmentStatus.PASSED && !Boolean.TRUE.equals(enrollment.getCertificateIssued())) {
            try {
                String schoolId = enrollment.getStudent().getUser().getSchool().getId();
                List<User> admins = userRepository.findAllBySchoolIdAndRoleName(schoolId, "SCHOOL_ADMIN");
                
                String studentName = enrollment.getStudent().getUser().getFirstName() + " " + enrollment.getStudent().getUser().getLastName();
                String courseName = enrollment.getCourse().getName();
                
                for (User admin : admins) {
                    notificationService.sendNotification(
                            admin.getId(),
                            "Certificat requis",
                            "L'étudiant " + studentName + " a réussi le cours " + courseName + ". Veuillez lui générer et uploader un certificat.",
                            NotificationType.COURSE_COMPLETED,
                            enrollmentId,
                            "Enrollment"
                    );
                }
                log.info("Notifications envoyées aux School Admins pour l'enrollment {}", enrollmentId);
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi des notifications pour enrollment {}", enrollmentId, e);
            }
        }

        return enrollmentMapper.toResponse(saved);
    }

    @Override
    public List<EnrollmentResponse> getPendingBySchoolId(String schoolId) {
        return enrollmentRepository.findAllByStudentUserSchoolIdAndStatus(schoolId, EnrollmentStatus.PENDING_APPROVAL)
                .stream().map(enrollmentMapper::toResponse).toList();
    }
}
