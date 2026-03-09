package com.langly.app.finance.service;

import com.langly.app.finance.web.dto.BillingConfirmRequest;
import com.langly.app.finance.web.dto.BillingResponse;

import java.util.List;

public interface BillingService {
    List<BillingResponse> getPendingBySchoolId(String schoolId);
    BillingResponse confirmPayment(String billingId, BillingConfirmRequest request);
    List<BillingResponse> getAllByStudentId(String studentId);
    List<BillingResponse> getAllBySchoolId(String schoolId);
}
