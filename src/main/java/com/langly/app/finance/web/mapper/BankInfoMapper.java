package com.langly.app.finance.web.mapper;

import com.langly.app.finance.entity.BankInfo;
import com.langly.app.finance.web.dto.BankInfoResponse;
import com.langly.app.finance.web.dto.BankInfoUpdateRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BankInfoMapper {

    BankInfoResponse toResponse(BankInfo entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget BankInfo entity, BankInfoUpdateRequest request);
}
