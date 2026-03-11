package com.langly.app.finance.repository;

import com.langly.app.finance.entity.BillingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingHistoryRepository extends JpaRepository<BillingHistory, String> {
    List<BillingHistory> findAllByBillingStudentId(String studentId);
    List<BillingHistory> findAllByBillingStudentUserSchoolId(String schoolId);
}
