package com.langly.app.finance.repository;

import com.langly.app.finance.entity.PaymentSchedule;
import com.langly.app.finance.entity.enums.InstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, String> {

    List<PaymentSchedule> findAllByInvoiceId(String invoiceId);

    List<PaymentSchedule> findAllByInvoiceIdOrderByInstallmentAsc(String invoiceId);

    List<PaymentSchedule> findAllByStatusAndDueDateBefore(InstallmentStatus status, LocalDate date);
}
