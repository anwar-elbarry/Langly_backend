package com.langly.app.finance.repository;

import com.langly.app.finance.entity.BillingSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillingSettingRepository extends JpaRepository<BillingSetting, String> {

    Optional<BillingSetting> findBySchoolId(String schoolId);
}
