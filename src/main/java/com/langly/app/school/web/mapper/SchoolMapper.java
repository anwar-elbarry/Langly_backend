package com.langly.app.school.web.mapper;

import com.langly.app.course.entity.enums.SchoolStatus;
import com.langly.app.school.entity.School;
import com.langly.app.school.web.dto.SchoolRequest;
import com.langly.app.school.web.dto.SchoolResponse;
import com.langly.app.school.web.dto.SchoolUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SchoolMapper {

    School toEntity(SchoolRequest request);

    School toUpdateEntity(SchoolUpdateRequest request);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    SchoolResponse toResponse(School school);

    void updateEntity(@MappingTarget School school, SchoolUpdateRequest request);

    @Named("statusToString")
    default String statusToString(SchoolStatus status) {
        return status != null ? status.name() : null;
    }
}
