package com.langly.app.student.service;

import com.langly.app.course.entity.Enrollment;
import com.langly.app.course.repository.EnrollmentRepository;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.shared.util.FileStorageService;
import com.langly.app.student.entity.Certification;
import com.langly.app.student.entity.Student;
import com.langly.app.student.repository.CertificationRepository;
import com.langly.app.student.web.dto.CertificationResponse;
import com.langly.app.student.web.mapper.CertificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificationMapper certificationMapper;
    private final PdfGeneratorService pdfGeneratorService;
    private final FileStorageService fileStorageService;

    @Override
    public List<CertificationResponse> getByStudentId(String studentId) {
        return certificationRepository.findAllByStudentId(studentId)
                .stream().map(certificationMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public CertificationResponse generateCertificate(String enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));

        Student student = enrollment.getStudent();
        var course = enrollment.getCourse();
        var school = course.getTeacher() != null ? course.getTeacher().getSchool() : null;

        String studentName = student.getUser().getFirstName() + " " + student.getUser().getLastName();
        String language = course.getLanguage();
        String level = course.getTargetLevel() != null ? course.getTargetLevel().name() : "";
        String schoolName = school != null ? school.getName() : "Langly";
        LocalDateTime issuedAt = LocalDateTime.now();

        // Générer le PDF
        String pdfPath = pdfGeneratorService.generateCertificate(
                studentName, course.getName(), language, level, schoolName, issuedAt);

        // Créer l'entité Certification
        Certification cert = new Certification();
        cert.setStudent(student);
        cert.setCourse(course);
        cert.setSchool(school);
        cert.setLanguage(language);
        cert.setLevel(course.getTargetLevel());
        cert.setIssuedAt(issuedAt);
        cert.setPdfUrl(pdfPath);

        // Marquer l'enrollment comme certifié
        enrollment.setCertificateIssued(true);
        enrollmentRepository.save(enrollment);

        Certification saved = certificationRepository.save(cert);
        log.info("Certificat généré pour l'étudiant {} (enrollment {})", studentName, enrollmentId);

        return certificationMapper.toResponse(saved);
    }

    @Override
    public Resource downloadCertificate(String certId) {
        Certification cert = certificationRepository.findById(certId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", certId));

        if (cert.getPdfUrl() == null) {
            throw new ResourceNotFoundException("Certificate PDF", certId);
        }

        return fileStorageService.loadAsResource(cert.getPdfUrl());
    }
}
