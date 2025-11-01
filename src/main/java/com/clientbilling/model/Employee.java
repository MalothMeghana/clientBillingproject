package com.clientbilling.model;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")

public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Employeeid;

    @Column(unique = true, nullable = false)
    private String username;
    private String password;

    @Column(unique = true, nullable = false)
    private String email;
    private String contactNumber;
    private String Designation;
    private String profileImage;
    private Long Adminid;

    private Long Clientid;
    private Long Teamleadid;
    private Long Projectid;


    private String role = "ROLE_EMPLOYEE";
    private String status;

 
}
