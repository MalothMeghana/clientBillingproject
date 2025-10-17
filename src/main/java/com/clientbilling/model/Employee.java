package com.clientbilling.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String empIdNo;
    private String username;
    private String password;
    private String designation;
    private String role = "ROLE_EMPLOYEE";
    private String status;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference("admin-employees")
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "teamlead_id")
    @JsonBackReference("teamlead-employees")
    private TeamLead teamLead;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference("project-employees")
    private Project project;
}
