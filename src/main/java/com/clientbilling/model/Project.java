package com.clientbilling.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects")

public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Projectid;


    private String projectName;
    private Double billingRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long Adminid;

    private Long Clientid;
    private Long Teamleadid;

  
}
