package com.langly.app.role.web.mapper;

import com.langly.app.user.entity.Role;
import com.langly.app.role.web.dto.request.RoleRequest;
import com.langly.app.role.web.dto.response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    Role toEntity(RoleRequest request);

    RoleResponse toResponse(Role role);

    @Mapping(target = "id", ignore = true)
    void updateEntity(RoleRequest request, @MappingTarget Role role);
}
