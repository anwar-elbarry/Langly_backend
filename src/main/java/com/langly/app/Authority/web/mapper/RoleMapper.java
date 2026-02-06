package com.langly.app.Authority.web.mapper;

import com.langly.app.Authority.entity.Role;
import com.langly.app.Authority.web.dto.request.RoleRequest;
import com.langly.app.Authority.web.dto.response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = PermissionMapper.class)
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    Role toEntity(RoleRequest request);

    RoleResponse toResponse(Role role);

    @Mapping(target = "id", ignore = true)
    void updateEntity(RoleRequest request, @MappingTarget Role role);
}
