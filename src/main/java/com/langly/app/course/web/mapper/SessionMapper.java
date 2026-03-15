package com.langly.app.course.web.mapper;

import com.langly.app.course.entity.Session;
import com.langly.app.course.entity.enums.AttendanceStatus;
import com.langly.app.course.web.dto.SessionRequest;
import com.langly.app.course.web.dto.SessionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = AttendanceStatus.class)
public interface SessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "attendanceRecords", ignore = true)
    Session toEntity(SessionRequest request);

    @Mapping(target = "mode", expression = "java(session.getMode() != null ? session.getMode().name() : null)")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    @Mapping(target = "presentCount", expression = "java(session.getAttendanceRecords() != null ? (int) session.getAttendanceRecords().stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE).count() : 0)")
    @Mapping(target = "totalEnrolled", expression = "java(session.getCourse() != null && session.getCourse().getEnrollments() != null ? session.getCourse().getEnrollments().size() : 0)")
    SessionResponse toResponse(Session session);
}
