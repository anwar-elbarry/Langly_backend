package com.langly.app.finance.web.mapper;

import com.langly.app.finance.entity.FeePayment;
import com.langly.app.finance.web.dto.FeePaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeePaymentMapper {

    @Mapping(target = "feeTemplateId", source = "feeTemplate.id")
    @Mapping(target = "feeTemplateName", source = "feeTemplate.name")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentFullName", expression = "java(feePayment.getStudent().getUser().getFirstName() + \" \" + feePayment.getStudent().getUser().getLastName())")
    FeePaymentResponse toResponse(FeePayment feePayment);
}
