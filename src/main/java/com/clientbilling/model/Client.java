package com.clientbilling.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")

public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Clientid;

    private String companyName;

    @Column(unique = true, nullable = false)
    private String email;

    private String address;
    private String contactNumber;
    private String profileImage;

    private String username;
    private String password;
    private String role = "ROLE_CLIENT";
    
    private Long adminid;
    
  
}
