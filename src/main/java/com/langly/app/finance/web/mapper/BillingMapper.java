package com.langly.app.finance.web.mapper;

import com.langly.app.finance.entity.Billing;
import com.langly.app.finance.entity.BillingHistory;
import com.langly.app.finance.web.dto.BillingHistoryResponse;
import com.langly.app.finance.web.dto.BillingResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BillingMapper {

    @Mapping(target = "status",          expression = "java(billing.getStatus() != null ? billing.getStatus().name() : null)")
    @Mapping(target = "paymentMethod",   expression = "java(billing.getPaymentMethod() != null ? billing.getPaymentMethod().name() : null)")
    @Mapping(target = "studentId",       source = "student.id")
    @Mapping(target = "studentFullName", expression = "java(billing.getStudent() != null && billing.getStudent().getUser() != null ? billing.getStudent().getUser().getFirstName() + \" \" + billing.getStudent().getUser().getLastName() : null)")
    @Mapping(target = "enrollmentId",    source = "enrollment.id")
    @Mapping(target = "courseId",         expression = "java(billing.getEnrollment() != null && billing.getEnrollment().getCourse() != null ? billing.getEnrollment().getCourse().getId() : null)")
    @Mapping(target = "courseName",      expression = "java(billing.getEnrollment() != null && billing.getEnrollment().getCourse() != null ? billing.getEnrollment().getCourse().getName() : null)")
    BillingResponse toResponse(Billing billing);

    @Mapping(target = "status",        expression = "java(history.getStatus() != null ? history.getStatus().name() : null)")
    @Mapping(target = "paymentMethod", expression = "java(history.getPaymentMethod() != null ? history.getPaymentMethod().name() : null)")
    @Mapping(target = "billingId",     source = "billing.id")
    BillingHistoryResponse toHistoryResponse(BillingHistory history);

    List<BillingHistoryResponse> toHistoryResponseList(List<BillingHistory> histories);
}
