package com.clientbilling.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientIdNo;
    private String companyName;
    private String contactEmail;
    private String address;

    // For login
    private String username;
    private String password;

    private String role = "ROLE_CLIENT";

    // Relationship with Admin
    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference("admin-clients")
    private Admin admin;

    // Relationship with Projects
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("client-projects")
    private List<Project> projects = new ArrayList<>();

    // Relationship with Invoices
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("client-invoices")
    private List<Invoice> invoices = new ArrayList<>();
}
