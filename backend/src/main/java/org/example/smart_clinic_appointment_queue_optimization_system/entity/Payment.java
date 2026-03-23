package org.example.smart_clinic_appointment_queue_optimization_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    //private String paymentStatus;
    private LocalDate paymentDate;
    private String paymentType;
    private String paymentStatus;
    private String transactionId;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
}
