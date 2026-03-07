package com.langly.app.user.web.mapper;

import com.langly.app.Authority.web.mapper.RoleMapper;
import com.langly.app.user.entity.User;
import com.langly.app.user.web.dto.request.UserRequest;
import com.langly.app.user.web.dto.request.UserUpdateRequest;
import com.langly.app.user.web.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = RoleMapper.class)
public interface UserMapper {

    @Mapping(target = "schoolId", source = "school.id")
    UserResponse toResponse(User user);

    @Mapping(target = "role.name", source = "roleName")
    User toEntity(UserRequest request);

    User toUpdateEntity(UserUpdateRequest request);
}
