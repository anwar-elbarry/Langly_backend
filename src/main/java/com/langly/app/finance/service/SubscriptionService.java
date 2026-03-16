package com.langly.app.finance.service;

import com.langly.app.finance.web.dto.*;

import java.util.List;

public interface SubscriptionService {

    SubscriptionResponse create(SubscriptionRequest request);

    SubscriptionResponse getById(String id);

    List<SubscriptionResponse> getBySchoolId(String schoolId);

    List<SubscriptionResponse> getAll();

    SubscriptionResponse update(String id, SubscriptionUpdateRequest request);

    SubscriptionResponse updatePaymentStatus(String id, PaymentStatusUpdateRequest request);

    void delete(String id);

    PaymentResponse pay(String id, SelectPaymentMethodRequest request);
    
    void declareTransfer(String id);
}
