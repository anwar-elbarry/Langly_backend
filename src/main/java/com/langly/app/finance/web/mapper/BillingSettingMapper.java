package com.langly.app.finance.web.mapper;

import com.langly.app.finance.entity.BillingSetting;
import com.langly.app.finance.web.dto.BillingSettingRequest;
import com.langly.app.finance.web.dto.BillingSettingResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BillingSettingMapper {

    @Mapping(target = "schoolId", source = "school.id")
    @Mapping(target = "defaultInstallmentPlan", expression = "java(entity.getDefaultInstallmentPlan() != null ? entity.getDefaultInstallmentPlan().name() : null)")
    BillingSettingResponse toResponse(BillingSetting entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "school", ignore = true)
    void updateEntity(@MappingTarget BillingSetting entity, BillingSettingRequest request);
}
