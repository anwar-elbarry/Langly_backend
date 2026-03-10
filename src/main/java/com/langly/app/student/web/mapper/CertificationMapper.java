package com.langly.app.student.web.mapper;

import com.langly.app.student.entity.Certification;
import com.langly.app.student.web.dto.CertificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CertificationMapper {

    @Mapping(target = "level", expression = "java(cert.getLevel() != null ? cert.getLevel().name() : null)")
    @Mapping(source = "course.name", target = "courseName")
    @Mapping(source = "school.name", target = "schoolName")
    CertificationResponse toResponse(Certification cert);
}
