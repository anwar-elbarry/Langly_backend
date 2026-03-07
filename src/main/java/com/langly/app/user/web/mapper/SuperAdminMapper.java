package com.langly.app.user.web.mapper;

import com.langly.app.user.entity.User;
import com.langly.app.user.web.dto.request.SuperAdminRequest;
import com.langly.app.user.web.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SuperAdminMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "school", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toEntity(SuperAdminRequest request);

    @Mapping(target = "schoolId", ignore = true)
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "school", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(SuperAdminRequest request, @MappingTarget User user);
}
