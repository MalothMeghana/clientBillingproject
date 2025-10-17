package com.clientbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clientbilling.model.Invoice;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByClientId(Long clientId);
    List<Invoice> findByProjectId(Long projectId);
}
