package com.langly.app.student.service;

import com.langly.app.student.web.dto.CertificationResponse;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * US07 : Service de gestion des certifications.
 */
public interface CertificationService {
    List<CertificationResponse> getByStudentId(String studentId);

    CertificationResponse generateCertificate(String enrollmentId);

    Resource downloadCertificate(String certId);
}
