package com.clientbilling.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teamleads")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String empIdNo = "";
    private String username;
    private String password;
    private String role = "ROLE_TEAMLEAD";

    // Relationships
    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference("admin-teamleads")
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference("client-teamleads")
    private Client client;

    @OneToMany(mappedBy = "teamLead", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("teamlead-employees")
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "teamLead", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("teamlead-projects")
    private List<Project> projects = new ArrayList<>();
}
