package com.langly.app.finance.web.mapper;

import com.langly.app.finance.entity.FeeTemplate;
import com.langly.app.finance.web.dto.FeeTemplateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeeTemplateMapper {

    @Mapping(target = "type", expression = "java(feeTemplate.getType() != null ? feeTemplate.getType().name() : null)")
    @Mapping(target = "schoolId", source = "school.id")
    FeeTemplateResponse toResponse(FeeTemplate feeTemplate);
}
