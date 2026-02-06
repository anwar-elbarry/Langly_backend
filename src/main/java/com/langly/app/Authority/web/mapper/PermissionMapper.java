package com.langly.app.Authority.web.mapper;

import com.langly.app.Authority.entity.Permission;
import com.langly.app.Authority.web.dto.response.PermissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {
    PermissionResponse toResponse(Permission entity);
}
