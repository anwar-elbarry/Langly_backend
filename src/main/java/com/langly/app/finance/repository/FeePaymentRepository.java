package com.langly.app.finance.repository;

import com.langly.app.finance.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, String> {

    List<FeePayment> findAllBySchoolId(String schoolId);

    List<FeePayment> findAllBySchoolIdAndStudentId(String schoolId, String studentId);

    List<FeePayment> findAllBySchoolIdAndStudentIdAndFeeTemplateId(String schoolId, String studentId, String feeTemplateId);
}
