package org.example.smart_clinic_appointment_queue_optimization_system.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
public class ScheduleRequestDto {
    private Long doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxSlots;
}
