package com.langly.app.course.service;

import com.langly.app.course.entity.Course;
import com.langly.app.course.entity.CourseMaterial;
import com.langly.app.course.entity.enums.MaterialType;
import com.langly.app.course.repository.CourseMaterialRepository;
import com.langly.app.course.repository.CourseRepository;
import com.langly.app.course.web.dto.CourseMaterialResponse;
import com.langly.app.course.web.mapper.CourseMaterialMapper;
import com.langly.app.exception.ResourceNotFoundException;
import com.langly.app.shared.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseMaterialServiceImpl implements CourseMaterialService {

    private final CourseMaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final CourseMaterialMapper materialMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public CourseMaterialResponse upload(String courseId, MultipartFile file) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        // Déterminer le type de matériel
        String contentType = file.getContentType();
        MaterialType type = MaterialType.PDF; // default
        if (contentType != null && contentType.startsWith("video/")) {
            type = MaterialType.VIDEO;
        }

        // Stocker le fichier
        String storedName = fileStorageService.store(file);

        // Créer l'entité
        CourseMaterial material = new CourseMaterial();
        material.setName(file.getOriginalFilename());
        material.setType(type);
        material.setFileUrl(storedName);
        material.setUploadedAt(LocalDateTime.now());
        material.setCourse(course);

        return materialMapper.toResponse(materialRepository.save(material));
    }

    @Override
    public List<CourseMaterialResponse> getAllByCourseId(String courseId) {
        return materialRepository.findAllByCourseId(courseId)
                .stream().map(materialMapper::toResponse).toList();
    }

    @Override
    public Resource download(String courseId, String materialId) {
        CourseMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseMaterial", materialId));

        if (!material.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Ce matériel n'appartient pas au cours spécifié");
        }

        return fileStorageService.loadAsResource(material.getFileUrl());
    }
}
