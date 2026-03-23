package org.example.smart_clinic_appointment_queue_optimization_system.service;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Appointment;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Payment;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public void processAppointmentPayment(Appointment app, double amount, String method, String transactionId) {
//        String status = method.equalsIgnoreCase("ONLINE") ? "PAID" : "PENDING";
//
//        Payment payment = Payment.builder()
//                .amount(amount)
//                .paymentStatus(status)
//                .paymentDate(LocalDate.now())
//                .appointment(appointment)
//                .build();
//
//        paymentRepository.save(payment);
        if (method == null) method = "CASH";
        Payment payment = Payment.builder()
                .appointment(app)
                .amount(amount)
                .paymentType(method)
                .transactionId(transactionId)
                .paymentDate(LocalDate.now())
                .paymentStatus(method.equalsIgnoreCase("CASH") ? "PENDING" : "PAID")
                .build();

          paymentRepository.save(payment);
    }
}
