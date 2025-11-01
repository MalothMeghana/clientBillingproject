package com.clientbilling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Paymentid;

    private String razorpayPaymentId;   // Razorpay payment ID
    private String razorpayOrderId;     // Razorpay order ID
    private String razorpaySignature;   // For verification
    private String status;              // Payment status (e.g., "captured", "failed")
    private Double amount;              // Payment amount in main currency unit (₹)

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;            // Linked invoice

    private Long paymentTimestamp;     // optional: timestamp of payment

	}