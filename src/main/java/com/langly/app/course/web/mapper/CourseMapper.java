package com.langly.app.course.web.mapper;

import com.langly.app.course.entity.Course;
import com.langly.app.course.web.dto.CourseRequest;
import com.langly.app.course.web.dto.CourseResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Course toEntity(CourseRequest request);

    @Mapping(target = "requiredLevel", expression = "java(course.getRequiredLevel() != null ? course.getRequiredLevel().name() : null)")
    @Mapping(target = "targetLevel",   expression = "java(course.getTargetLevel()   != null ? course.getTargetLevel().name()   : null)")
    @Mapping(target = "teacherId",       source = "teacher.id")
    @Mapping(target = "teacherFullName", expression = "java(course.getTeacher() != null ? course.getTeacher().getFirstName() + \" \" + course.getTeacher().getLastName() : null)")
    @Mapping(target = "enrolledCount",   expression = "java(course.getEnrollments() != null ? course.getEnrollments().size() : 0)")
    CourseResponse toResponse(Course course);
}
