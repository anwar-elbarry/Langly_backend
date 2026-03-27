package com.langly.app.course.controller;

import com.langly.app.course.service.CourseMaterialService;
import com.langly.app.course.web.dto.CourseMaterialResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1/courses/{courseId}/materials")
@RequiredArgsConstructor
@Tag(name = "Course Materials", description = "US06 : Matériels de cours (PDF, vidéo)")
public class CourseMaterialController {

    private final CourseMaterialService materialService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    @Operation(summary = "Uploader un matériel", description = "Envoie un fichier PDF ou vidéo associé à un cours")
    public ResponseEntity<CourseMaterialResponse> upload(
            @PathVariable String courseId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(201).body(materialService.upload(courseId, file));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN','STUDENT')")
    @Operation(summary = "Lister les matériels", description = "Retourne la liste des matériels d'un cours")
    public ResponseEntity<List<CourseMaterialResponse>> list(@PathVariable String courseId) {
        return ResponseEntity.ok(materialService.getAllByCourseId(courseId));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN','STUDENT')")
    @Operation(summary = "Télécharger un matériel", description = "Télécharge le fichier d'un matériel de cours")
    public ResponseEntity<Resource> download(
            @PathVariable String courseId,
            @PathVariable String id) {
        Resource resource = materialService.download(courseId, id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
