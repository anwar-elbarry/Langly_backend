package com.langly.app.student.service;

import com.langly.app.student.web.dto.CertificationResponse;
import org.springframework.core.io.Resource;

import java.util.List;

public interface CertificationService {
    List<CertificationResponse> getByStudentId(String studentId);

    CertificationResponse uploadCertificate(org.springframework.web.multipart.MultipartFile file, String enrollmentId);

    Resource downloadCertificate(String certId);
}
