package com.langly.app.course.web.mapper;

import com.langly.app.course.entity.CourseMaterial;
import com.langly.app.course.web.dto.CourseMaterialResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMaterialMapper {

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(target = "type", expression = "java(material.getType() != null ? material.getType().name() : null)")
    CourseMaterialResponse toResponse(CourseMaterial material);
}
