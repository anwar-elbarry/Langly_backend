package com.langly.app.finance.service;

import com.langly.app.finance.web.dto.FeePaymentRequest;
import com.langly.app.finance.web.dto.FeePaymentResponse;
import com.langly.app.finance.web.dto.StudentFeeStatusResponse;

import java.util.List;

public interface FeePaymentService {

    FeePaymentResponse recordPayment(String schoolId, FeePaymentRequest request);

    List<StudentFeeStatusResponse> getStudentFeeStatuses(String schoolId, String studentId);

    List<FeePaymentResponse> getPaymentHistory(String schoolId, String studentId, String feeTemplateId);

    void closeRecurringFee(String schoolId, String studentId, String feeTemplateId);

    void deletePayment(String paymentId);
}
