package com.clientbilling.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoices")

public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Invoiceid;

   
    private String month;
    private Double totalHours;
    private Double netAmount;
    private  String status;
    private Long Projectid;
    

 
}
