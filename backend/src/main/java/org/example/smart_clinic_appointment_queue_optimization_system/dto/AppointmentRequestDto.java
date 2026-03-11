package org.example.smart_clinic_appointment_queue_optimization_system.dto;

import lombok.Data;

@Data
public class AppointmentRequestDto {

    private Long doctorId;
    private Long patientId;
    private Long scheduleId;
}
