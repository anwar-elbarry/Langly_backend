package com.langly.app.course.web.mapper;

import com.langly.app.course.entity.Attendance;
import com.langly.app.course.web.dto.AttendanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(target = "studentFullName", expression = "java(attendance.getStudent().getUser().getFirstName() + \" \" + attendance.getStudent().getUser().getLastName())")
    @Mapping(source = "session.id", target = "sessionId")
    @Mapping(target = "status", expression = "java(attendance.getStatus() != null ? attendance.getStatus().name() : null)")
    AttendanceResponse toResponse(Attendance attendance);
}
