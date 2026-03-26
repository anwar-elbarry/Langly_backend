package com.langly.app.course.service;

import com.langly.app.course.entity.Course;
import com.langly.app.course.entity.Enrollment;
import com.langly.app.course.entity.enums.EnrollmentStatus;
import com.langly.app.course.entity.enums.Level;
import com.langly.app.course.repository.CourseRepository;
import com.langly.app.course.repository.EnrollmentRepository;
import com.langly.app.course.web.dto.EnrollmentRequest;
import com.langly.app.course.web.dto.EnrollmentResponse;
import com.langly.app.course.web.mapper.EnrollmentMapper;
import com.langly.app.exception.AlreadyExistsException;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.finance.repository.BillingRepository;
import com.langly.app.finance.service.InvoiceService;
import com.langly.app.notification.service.NotificationService;
import com.langly.app.school.entity.School;
import com.langly.app.student.entity.Student;
import com.langly.app.student.repository.StudentRepository;
import com.langly.app.user.entity.User;
import com.langly.app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private EnrollmentMapper enrollmentMapper;
    @Mock private NotificationService notificationService;
    @Mock private UserRepository userRepository;
    @Mock private InvoiceService invoiceService;
    @Mock private BillingRepository billingRepository;

    @InjectMocks private EnrollmentServiceImpl enrollmentService;

    private Student student;
    private Course course;
    private User user;
    private School school;
    private Enrollment enrollment;
    private EnrollmentRequest enrollmentRequest;
    private EnrollmentResponse enrollmentResponse;

    @BeforeEach
    void setUp() {
        school = new School();
        school.setId("school-1");

        user = new User();
        user.setId("user-1");
        user.setFirstName("Alice");
        user.setLastName("Martin");
        user.setSchool(school);

        student = new Student();
        student.setId("student-1");
        student.setUser(user);
        student.setLevel(Level.A2);

        course = new Course();
        course.setId("course-1");
        course.setName("English B1");
        course.setRequiredLevel(Level.A2);
        course.setCapacity(20);
        course.setPrice(BigDecimal.valueOf(1500));

        enrollment = new Enrollment();
        enrollment.setId("enrollment-1");
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        enrollment.setEnrolledAt(LocalDate.now());
        enrollment.setCertificateIssued(false);

        enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.setStudentId("student-1");
        enrollmentRequest.setCourseId("course-1");

        enrollmentResponse = new EnrollmentResponse();
        enrollmentResponse.setId("enrollment-1");
        enrollmentResponse.setStatus("IN_PROGRESS");
        enrollmentResponse.setStudentId("student-1");
        enrollmentResponse.setCourseId("course-1");
    }

    @Nested
    @DisplayName("enroll()")
    class Enroll {

        @Test
        @DisplayName("should enroll student successfully")
        void shouldEnrollStudent() {
            when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(false);
            when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
            when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
            when(enrollmentRepository.findAllByCourseId("course-1")).thenReturn(List.of());
            when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
            when(enrollmentMapper.toResponse(enrollment)).thenReturn(enrollmentResponse);

            EnrollmentResponse result = enrollmentService.enroll(enrollmentRequest);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo("IN_PROGRESS");
            verify(enrollmentRepository).save(any(Enrollment.class));
        }

        @Test
        @DisplayName("should throw AlreadyExistsException when already enrolled")
        void shouldThrowWhenAlreadyEnrolled() {
            when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(true);

            assertThatThrownBy(() -> enrollmentService.enroll(enrollmentRequest))
                    .isInstanceOf(AlreadyExistsException.class);

            verify(enrollmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when student not found")
        void shouldThrowWhenStudentNotFound() {
            when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(false);
            when(studentRepository.findById("student-1")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> enrollmentService.enroll(enrollmentRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when course not found")
        void shouldThrowWhenCourseNotFound() {
            when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(false);
            when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
            when(courseRepository.findById("course-1")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> enrollmentService.enroll(enrollmentRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("should throw when student level is null")
        void shouldThrowWhenStudentLevelNull() {
            student.setLevel(null);

            when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(false);
            when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
            when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));

            assertThatThrownBy(() -> enrollmentService.enroll(enrollmentRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("niveau");
        }

        @Test
        @DisplayName("should throw when student level does not match course required level")
        void shouldThrowWhenLevelMismatch() {
            student.setLevel(Level.B2);

            when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(false);
            when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
            when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));

            assertThatThrownBy(() -> enrollmentService.enroll(enrollmentRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("niveau");
        }

        @Test
        @DisplayName("should throw when course is full")
        void shouldThrowWhenCourseFull() {
            course.setCapacity(1);
            Enrollment existing = new Enrollment();

            when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(false);
            when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
            when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
            when(enrollmentRepository.findAllByCourseId("course-1")).thenReturn(List.of(existing));

            assertThatThrownBy(() -> enrollmentService.enroll(enrollmentRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("capacité");
        }
    }

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("should return enrollment when found")
        void shouldReturnEnrollment() {
            when(enrollmentRepository.findById("enrollment-1")).thenReturn(Optional.of(enrollment));
            when(enrollmentMapper.toResponse(enrollment)).thenReturn(enrollmentResponse);

            EnrollmentResponse result = enrollmentService.getById("enrollment-1");

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("enrollment-1");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(enrollmentRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> enrollmentService.getById("unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllByStudentId()")
    class GetAllByStudentId {

        @Test
        @DisplayName("should return all enrollments for a student")
        void shouldReturnEnrollments() {
            when(enrollmentRepository.findAllByStudentId("student-1")).thenReturn(List.of(enrollment));
            when(enrollmentMapper.toResponse(enrollment)).thenReturn(enrollmentResponse);

            List<EnrollmentResponse> result = enrollmentService.getAllByStudentId("student-1");

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getAllByCourseId()")
    class GetAllByCourseId {

        @Test
        @DisplayName("should return all enrollments for a course")
        void shouldReturnEnrollments() {
            when(enrollmentRepository.findAllByCourseId("course-1")).thenReturn(List.of(enrollment));
            when(enrollmentMapper.toResponse(enrollment)).thenReturn(enrollmentResponse);

            List<EnrollmentResponse> result = enrollmentService.getAllByCourseId("course-1");

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getAllBySchoolId()")
    class GetAllBySchoolId {

        @Test
        @DisplayName("should return all enrollments for a school")
        void shouldReturnEnrollments() {
            when(enrollmentRepository.findAllByStudentUserSchoolId("school-1")).thenReturn(List.of(enrollment));
            when(enrollmentMapper.toResponse(enrollment)).thenReturn(enrollmentResponse);

            List<EnrollmentResponse> result = enrollmentService.getAllBySchoolId("school-1");

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("updateStatus()")
    class UpdateStatus {

        @Test
        @DisplayName("should update enrollment status to PASSED")
        void shouldUpdateStatusToPassed() {
            when(enrollmentRepository.findById("enrollment-1")).thenReturn(Optional.of(enrollment));
            when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
            when(enrollmentMapper.toResponse(enrollment)).thenReturn(enrollmentResponse);
            when(userRepository.findAllBySchoolIdAndRoleName("school-1", "SCHOOL_ADMIN")).thenReturn(List.of());

            EnrollmentResponse result = enrollmentService.updateStatus("enrollment-1", EnrollmentStatus.PASSED);

            assertThat(result).isNotNull();
            assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.PASSED);
        }

        @Test
        @DisplayName("should set leftAt when status is WITHDRAWN")
        void shouldSetLeftAtWhenWithdrawn() {
            when(enrollmentRepository.findById("enrollment-1")).thenReturn(Optional.of(enrollment));
            when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
            when(enrollmentMapper.toResponse(enrollment)).thenReturn(enrollmentResponse);

            enrollmentService.updateStatus("enrollment-1", EnrollmentStatus.WITHDRAWN);

            assertThat(enrollment.getLeftAt()).isEqualTo(LocalDate.now());
            assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.WITHDRAWN);
        }

        @Test
        @DisplayName("should set leftAt when status is FAILED")
        void shouldSetLeftAtWhenFailed() {
            when(enrollmentRepository.findById("enrollment-1")).thenReturn(Optional.of(enrollment));
            when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
            when(enrollmentMapper.toResponse(enrollment)).thenReturn(enrollmentResponse);

            enrollmentService.updateStatus("enrollment-1", EnrollmentStatus.FAILED);

            assertThat(enrollment.getLeftAt()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("should throw when enrollment not found")
        void shouldThrowWhenNotFound() {
            when(enrollmentRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> enrollmentService.updateStatus("unknown", EnrollmentStatus.PASSED))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("rejectEnrollment()")
    class RejectEnrollment {

        @Test
        @DisplayName("should reject a pending enrollment")
        void shouldRejectPendingEnrollment() {
            enrollment.setStatus(EnrollmentStatus.PENDING_APPROVAL);

            when(enrollmentRepository.findById("enrollment-1")).thenReturn(Optional.of(enrollment));
            when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
            when(enrollmentMapper.toResponse(enrollment)).thenReturn(enrollmentResponse);

            EnrollmentResponse result = enrollmentService.rejectEnrollment("enrollment-1");

            assertThat(result).isNotNull();
            assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.REJECTED);
            assertThat(enrollment.getLeftAt()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("should throw when enrollment is not PENDING_APPROVAL")
        void shouldThrowWhenNotPending() {
            enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);

            when(enrollmentRepository.findById("enrollment-1")).thenReturn(Optional.of(enrollment));

            assertThatThrownBy(() -> enrollmentService.rejectEnrollment("enrollment-1"))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("getPendingBySchoolId()")
    class GetPendingBySchoolId {

        @Test
        @DisplayName("should return pending enrollments for a school")
        void shouldReturnPendingEnrollments() {
            when(enrollmentRepository.findAllByStudentUserSchoolIdAndStatus("school-1", EnrollmentStatus.PENDING_APPROVAL))
                    .thenReturn(List.of(enrollment));
            when(enrollmentMapper.toResponse(enrollment)).thenReturn(enrollmentResponse);

            List<EnrollmentResponse> result = enrollmentService.getPendingBySchoolId("school-1");

            assertThat(result).hasSize(1);
        }
    }
}
