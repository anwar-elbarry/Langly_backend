package com.langly.app.course.service;

import com.langly.app.course.entity.enums.EnrollmentStatus;
import com.langly.app.course.web.dto.EnrollmentRequest;
import com.langly.app.course.web.dto.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse enroll(EnrollmentRequest request);

    EnrollmentResponse getById(String id);

    List<EnrollmentResponse> getAllByStudentId(String studentId);

    List<EnrollmentResponse> getAllByCourseId(String courseId);

    List<EnrollmentResponse> getAllBySchoolId(String schoolId);

    /**
     * US07 : Met à jour le statut d'une inscription. Si PASSED → génère un
     * certificat.
     */
    EnrollmentResponse updateStatus(String enrollmentId, EnrollmentStatus status);

    /**
     * Student requests enrollment in a course.
     * Creates enrollment with status PENDING_APPROVAL.
     */
    EnrollmentResponse requestEnrollment(String studentId, String courseId);

    /**
     * Admin approves a pending enrollment request.
     * Sets status to APPROVED and generates an invoice.
     */
    EnrollmentResponse approveEnrollment(String enrollmentId, java.util.List<String> discountIds);

    /**
     * Admin rejects a pending enrollment request.
     * Sets status to REJECTED.
     */
    EnrollmentResponse rejectEnrollment(String enrollmentId);

    /**
     * Get pending enrollment requests for a school.
     */
    List<EnrollmentResponse> getPendingBySchoolId(String schoolId);
}
