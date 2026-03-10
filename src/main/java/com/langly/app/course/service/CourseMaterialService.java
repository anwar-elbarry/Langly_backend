package com.langly.app.course.service;

import com.langly.app.course.web.dto.CourseMaterialResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * US06 : Service de gestion des matériels de cours.
 */
public interface CourseMaterialService {
    CourseMaterialResponse upload(String courseId, MultipartFile file);

    List<CourseMaterialResponse> getAllByCourseId(String courseId);

    Resource download(String courseId, String materialId);
}
