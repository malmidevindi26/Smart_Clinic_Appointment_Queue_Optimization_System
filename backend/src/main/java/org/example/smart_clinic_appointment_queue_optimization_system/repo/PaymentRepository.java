package org.example.smart_clinic_appointment_queue_optimization_system.repo;

import org.example.smart_clinic_appointment_queue_optimization_system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Calculate total revenue from completed payments
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'PAID'")
    Double calculateTotalRevenue();

    // Doctor-wise income
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.appointment.doctor.id = :doctorId AND p.paymentStatus = 'PAID'")
    Double calculateRevenueByDoctor(@Param("doctorId") Long doctorId);

    // Daily income
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentDate = :date AND p.paymentStatus = 'PAID'")
    Double calculateDailyRevenue(@Param("date") LocalDate date);
}
