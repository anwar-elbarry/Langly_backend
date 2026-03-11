package com.langly.app.finance.repository;

import com.langly.app.finance.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {

    List<Discount> findAllBySchoolId(String schoolId);

    List<Discount> findAllBySchoolIdAndIsActiveTrue(String schoolId);
}
