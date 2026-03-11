package com.langly.app.finance.repository;

import com.langly.app.finance.entity.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, String> {

    List<InvoiceLine> findAllByInvoiceId(String invoiceId);
}
