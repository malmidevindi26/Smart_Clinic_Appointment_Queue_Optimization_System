package org.example.smart_clinic_appointment_queue_optimization_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDto {

    private Long id;
    private String doctorName;
    private String patientName;
    private int queueNumber;
    private LocalDate appointmentDate;
    private String status;
    private boolean isPriority;
}