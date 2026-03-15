package com.langly.app.finance.service;

import com.langly.app.finance.entity.enums.InstallmentPlan;
import com.langly.app.finance.web.dto.FinancialSummaryResponse;
import com.langly.app.finance.web.dto.InvoiceResponse;
import com.langly.app.finance.web.dto.PaymentScheduleResponse;
import com.langly.app.finance.web.dto.RecordPaymentRequest;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse generateInvoice(String enrollmentId, List<String> discountIds);

    InvoiceResponse getById(String invoiceId);

    List<InvoiceResponse> getAllBySchoolId(String schoolId);

    List<InvoiceResponse> getAllByStudentId(String studentId);

    InvoiceResponse recordPayment(String invoiceId, RecordPaymentRequest request);

    List<PaymentScheduleResponse> createInstallmentPlan(String invoiceId, InstallmentPlan plan);

    List<PaymentScheduleResponse> getSchedule(String invoiceId);

    FinancialSummaryResponse getFinancialSummary(String schoolId);
}
