package org.example.smart_clinic_appointment_queue_optimization_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorForecastDto {
    private Long doctorId;
    private String doctorName;
    private String specialization;

    private double historicalAverage;
    private double predictedAppointments;
    private String trend;
    private double trendPercentage;
}
