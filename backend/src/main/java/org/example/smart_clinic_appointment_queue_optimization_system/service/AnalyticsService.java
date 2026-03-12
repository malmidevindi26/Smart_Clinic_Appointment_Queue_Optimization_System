package org.example.smart_clinic_appointment_queue_optimization_system.service;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final PaymentRepository paymentRepository;

    public Double getTotalRevenue() {
        Double total = paymentRepository.calculateTotalRevenue();
        return (total != null) ? total : 0.0;
    }

    public Double getDoctorRevenue(Long doctorId) {
        Double total = paymentRepository.calculateRevenueByDoctor(doctorId);
        return (total != null) ? total : 0.0;
    }

    public Double getTodayRevenue() {

        Double total = paymentRepository.calculateDailyRevenue(java.time.LocalDate.now());
        return (total != null) ? total : 0.0;
    }
}
