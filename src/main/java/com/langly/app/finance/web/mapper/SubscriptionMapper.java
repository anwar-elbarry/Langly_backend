package com.langly.app.finance.web.mapper;

import com.langly.app.finance.entity.Subscription;
import com.langly.app.finance.entity.enums.BillingCycle;
import com.langly.app.finance.entity.enums.PaymentStatus;
import com.langly.app.finance.web.dto.SubscriptionRequest;
import com.langly.app.finance.web.dto.SubscriptionResponse;
import com.langly.app.finance.web.dto.SubscriptionUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubscriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "school", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentPeriodStart", ignore = true)
    @Mapping(target = "currentPeriodEnd", ignore = true)
    @Mapping(target = "nextPaymentDate", ignore = true)
    Subscription toEntity(SubscriptionRequest request);

    @Mapping(target = "schoolId", source = "school.id")
    @Mapping(target = "schoolName", source = "school.name")
    @Mapping(target = "billingCycle", source = "billingCycle", qualifiedByName = "billingCycleToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    SubscriptionResponse toResponse(Subscription subscription);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "school", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentPeriodStart", ignore = true)
    @Mapping(target = "currentPeriodEnd", ignore = true)
    @Mapping(target = "nextPaymentDate", ignore = true)
    void updateEntity(@MappingTarget Subscription subscription, SubscriptionUpdateRequest request);

    @Named("billingCycleToString")
    default String billingCycleToString(BillingCycle billingCycle) {
        return billingCycle != null ? billingCycle.name() : null;
    }

    @Named("statusToString")
    default String statusToString(PaymentStatus status) {
        return status != null ? status.name() : null;
    }
}
