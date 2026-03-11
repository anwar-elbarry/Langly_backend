package com.langly.app.finance.repository;

import com.langly.app.finance.entity.Invoice;
import com.langly.app.finance.entity.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {

    List<Invoice> findAllBySchoolId(String schoolId);

    List<Invoice> findAllByStudentId(String studentId);

    List<Invoice> findAllBySchoolIdAndStatus(String schoolId, InvoiceStatus status);

    Optional<Invoice> findByEnrollmentId(String enrollmentId);

    long countBySchoolId(String schoolId);
}
