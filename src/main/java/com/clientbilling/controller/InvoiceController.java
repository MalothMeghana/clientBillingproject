package com.clientbilling.controller;

import com.clientbilling.model.Invoice;
import com.clientbilling.service.InvoiceService;
import com.clientbilling.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/generate")
    public ResponseEntity<?> generateInvoice(@RequestBody Invoice invoice) {
        String role = securityUtil.getCurrentUserRole();
        if(!role.equals("ROLE_ADMIN") && !role.equals("ROLE_CLIENT") && !role.equals("ROLE_TEAMLEAD"))
            return ResponseEntity.status(403).body("Access Denied");
        return ResponseEntity.ok(invoiceService.generateInvoice(invoice));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllInvoices() {
        String role = securityUtil.getCurrentUserRole();
        if(!role.equals("ROLE_ADMIN") && !role.equals("ROLE_CLIENT") && !role.equals("ROLE_TEAMLEAD") && !role.equals("ROLE_EMPLOYEE"))
            return ResponseEntity.status(403).body("Access Denied");
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceById(@PathVariable Long id) {
        String role = securityUtil.getCurrentUserRole();
        if(!role.equals("ROLE_ADMIN") && !role.equals("ROLE_CLIENT") && !role.equals("ROLE_TEAMLEAD") && !role.equals("ROLE_EMPLOYEE"))
            return ResponseEntity.status(403).body("Access Denied");
        Invoice invoice = invoiceService.getInvoiceById(id);
        if(invoice != null) return ResponseEntity.ok(invoice);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id) {
        if(!"ROLE_ADMIN".equals(securityUtil.getCurrentUserRole()))
            return ResponseEntity.status(403).body("Access Denied");
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok("Invoice deleted successfully");
    }
}
