package com.clientbilling.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clientbilling.model.Invoice;
import com.clientbilling.model.Project;
import com.clientbilling.repository.InvoiceRepository;
import com.clientbilling.repository.ProjectRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * ✅ Generate Invoice PDF using the most recent project.
     * Works even without entity mapping or client relationships.
     */
    @Transactional
    public ByteArrayInputStream generateInvoicePDF(Invoice invoice) {
        try {
            // 🔹 Fetch the latest project from DB
            Project latestProject = projectRepository.findAll().stream()
                    .reduce((first, second) -> second) // Get the last project
                    .orElseThrow(() -> new RuntimeException("No project found in the database"));

            // 🔹 Calculate net amount using project's billing rate (if available)
            Double billingRate = latestProject.getBillingRate() != null ? latestProject.getBillingRate() : 0.0;
            Double totalHours = invoice.getTotalHours() != null ? invoice.getTotalHours() : 0.0;
            invoice.setNetAmount(totalHours * billingRate);

            // 🔹 Default status
            if (invoice.getStatus() == null || invoice.getStatus().isEmpty()) {
                invoice.setStatus("Pending");
            }

            // 🔹 Save invoice record in DB
            invoiceRepository.save(invoice);

            // 🔹 Generate PDF content
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // === PDF HEADER ===
            document.add(new Paragraph("INVOICE")
                    .setBold()
                    .setFontSize(20)
                    .setMarginBottom(10));

            // === INVOICE DETAILS ===
            document.add(new Paragraph("Invoice ID: " + (invoice.getInvoiceid() != null ? invoice.getInvoiceid() : "N/A")));
            document.add(new Paragraph("Month: " + (invoice.getMonth() != null ? invoice.getMonth() : "N/A")));
            document.add(new Paragraph("Total Hours: " + totalHours));
            document.add(new Paragraph("Net Amount: $" + invoice.getNetAmount()));
            document.add(new Paragraph("Status: " + invoice.getStatus()));

            // === PROJECT DETAILS ===
            document.add(new Paragraph("\n--- Project Details ---"));
            document.add(new Paragraph("Project Name: " + 
                (latestProject.getProjectName() != null ? latestProject.getProjectName() : "N/A")));
            document.add(new Paragraph("Billing Rate: $" + billingRate));

            // Optional fields
           
            document.close();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating invoice PDF: " + e.getMessage(), e);
        }
    }

    /** ✅ Get all invoices */
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    /** ✅ Get invoice by ID */
    @Transactional(readOnly = true)
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    /** ✅ Delete invoice */
    @Transactional
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new RuntimeException("Invoice not found with ID: " + id);
        }
        invoiceRepository.deleteById(id);
    }
}
