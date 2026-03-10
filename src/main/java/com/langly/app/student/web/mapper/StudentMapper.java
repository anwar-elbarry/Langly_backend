package com.langly.app.student.web.mapper;

import com.langly.app.student.entity.Student;
import com.langly.app.student.web.dto.StudentResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    @Mapping(target = "userId",        source = "user.id")
    @Mapping(target = "firstName",     source = "user.firstName")
    @Mapping(target = "lastName",      source = "user.lastName")
    @Mapping(target = "email",         source = "user.email")
    @Mapping(target = "phoneNumber",   source = "user.phoneNumber")
    @Mapping(target = "cnie",          source = "CNIE")
    @Mapping(target = "level",         expression = "java(student.getLevel() != null ? student.getLevel().name() : null)")
    @Mapping(target = "gender",        expression = "java(student.getGender() != null ? student.getGender().name() : null)")
    @Mapping(target = "missingFields", ignore = true)
    StudentResponse toResponse(Student student);
}
