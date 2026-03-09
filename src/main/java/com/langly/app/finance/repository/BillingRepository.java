package com.langly.app.finance.repository;

import com.langly.app.finance.entity.Billing;
import com.langly.app.finance.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingRepository extends JpaRepository<Billing, String> {
    List<Billing> findAllByStatus(PaymentStatus status);
    List<Billing> findAllByStudentId(String studentId);
    List<Billing> findAllByStudentUserSchoolId(String schoolId);
    List<Billing> findAllByStudentUserSchoolIdAndStatus(String schoolId, PaymentStatus status);
}
