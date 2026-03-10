package com.langly.app.course.web.mapper;

import com.langly.app.course.entity.Enrollment;
import com.langly.app.course.web.dto.EnrollmentResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EnrollmentMapper {

    @Mapping(target = "status",          expression = "java(enrollment.getStatus() != null ? enrollment.getStatus().name() : null)")
    @Mapping(target = "studentId",       source = "student.id")
    @Mapping(target = "studentFullName", expression = "java(enrollment.getStudent() != null && enrollment.getStudent().getUser() != null ? enrollment.getStudent().getUser().getFirstName() + \" \" + enrollment.getStudent().getUser().getLastName() : null)")
    @Mapping(target = "courseId",        source = "course.id")
    @Mapping(target = "courseName",      source = "course.name")
    @Mapping(target = "level",           expression = "java(enrollment.getStudent() != null && enrollment.getStudent().getLevel() != null ? enrollment.getStudent().getLevel().name() : null)")
    @Mapping(target = "coursePrice",     source = "course.price")
    EnrollmentResponse toResponse(Enrollment enrollment);
}
