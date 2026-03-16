package com.langly.app.student.service;

import com.langly.app.student.web.dto.CertificationResponse;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * US07 : Service de gestion des certifications.
 */
public interface CertificationService {
    List<CertificationResponse> getByStudentId(String studentId);

    /**
     * School Admin uploads a PDF certificate for an enrollment.
     */
    CertificationResponse uploadCertificate(org.springframework.web.multipart.MultipartFile file, String enrollmentId);

    Resource downloadCertificate(String certId);
}
