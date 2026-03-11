package com.langly.app.finance.web.mapper;

import com.langly.app.finance.entity.Discount;
import com.langly.app.finance.web.dto.DiscountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DiscountMapper {

    @Mapping(target = "type", expression = "java(discount.getType() != null ? discount.getType().name() : null)")
    DiscountResponse toResponse(Discount discount);
}
