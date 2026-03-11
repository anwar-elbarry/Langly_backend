package com.langly.app.finance.repository;

import com.langly.app.finance.entity.FeeTemplate;
import com.langly.app.finance.entity.enums.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeTemplateRepository extends JpaRepository<FeeTemplate, String> {

    List<FeeTemplate> findAllBySchoolId(String schoolId);

    List<FeeTemplate> findAllBySchoolIdAndIsActiveTrue(String schoolId);

    List<FeeTemplate> findAllBySchoolIdAndType(String schoolId, FeeType type);
}
