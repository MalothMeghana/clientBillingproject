package com.clientbilling.controller;

import com.clientbilling.model.Invoice;
import com.clientbilling.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    // ✅ Generate Invoice PDF — accessible by ADMIN, CLIENT, or TEAMLEAD
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'TEAMLEAD')")
    @PostMapping("/generate")
    public ResponseEntity<?> generateInvoice(@RequestBody Invoice invoice) {
        try {
            // Service will handle PDF generation
            InputStreamResource pdfStream = new InputStreamResource(invoiceService.generateInvoicePDF(invoice));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfStream);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating invoice PDF: " + e.getMessage());
        }
    }
}
