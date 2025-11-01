package com.clientbilling.repository;

import com.clientbilling.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
}
