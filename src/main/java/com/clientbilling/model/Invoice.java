package com.clientbilling.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceId;
    private String clientIdNo;
    private String projectIdNo;
    private String month;
    private Double totalHours;
    private Double netAmount;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference("admin-invoices")
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference("client-invoices")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference("project-invoices")
    private Project project;
}
