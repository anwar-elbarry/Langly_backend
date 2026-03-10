package com.langly.app.course.web.mapper;

import com.langly.app.course.entity.Session;
import com.langly.app.course.web.dto.SessionRequest;
import com.langly.app.course.web.dto.SessionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "qrToken", ignore = true)
    @Mapping(target = "qrExpiresAt", ignore = true)
    @Mapping(target = "attendanceRecords", ignore = true)
    Session toEntity(SessionRequest request);

    @Mapping(target = "mode", expression = "java(session.getMode() != null ? session.getMode().name() : null)")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    SessionResponse toResponse(Session session);
}
