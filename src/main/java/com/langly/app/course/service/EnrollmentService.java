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

    EnrollmentResponse updateStatus(String enrollmentId, EnrollmentStatus status);

    EnrollmentResponse requestEnrollment(String studentId, String courseId);

    EnrollmentResponse approveEnrollment(String enrollmentId, java.util.List<String> discountIds);

    EnrollmentResponse rejectEnrollment(String enrollmentId);

    List<EnrollmentResponse> getPendingBySchoolId(String schoolId);
}
