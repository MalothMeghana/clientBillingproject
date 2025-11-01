package com.clientbilling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teamleads")

public class TeamLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Teamleadid;

   
    private String username;
    private String password;

    @Column(unique = true, nullable = false)
    private String email;
    private String contactNumber;
    private String profileImage;

    private String role = "ROLE_TEAMLEAD";
    
    private Long Adminid;

    private Long Clientid;

}
