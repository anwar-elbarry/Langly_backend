package com.langly.app.student.controller;

import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.student.repository.StudentRepository;
import com.langly.app.student.service.CertificationService;
import com.langly.app.student.web.dto.CertificationResponse;
import com.langly.app.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student/certifications")
@RequiredArgsConstructor
@Tag(name = "Student — Certifications", description = "US07 : Certificats auto-générés")
public class StudentCertificationController {

    private final CertificationService certificationService;
    private final StudentRepository studentRepository;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mes certificats", description = "Retourne la liste des certificats de l'étudiant connecté")
    public ResponseEntity<List<CertificationResponse>> getMyCertifications(@AuthenticationPrincipal User user) {
        String studentId = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId=" + user.getId()))
                .getId();
        return ResponseEntity.ok(certificationService.getByStudentId(studentId));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Télécharger un certificat", description = "Télécharge le fichier PDF du certificat")
    public ResponseEntity<Resource> download(@PathVariable String id) {
        Resource resource = certificationService.downloadCertificate(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SCHOOL_ADMIN')")
    @Operation(summary = "Uploader un certificat", description = "L'admin uploade un PDF pour un étudiant ayant réussi un cours")
    public ResponseEntity<CertificationResponse> uploadCertificate(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam("enrollmentId") String enrollmentId) {
        return ResponseEntity.ok(certificationService.uploadCertificate(file, enrollmentId));
    }
}
