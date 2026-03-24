package org.example.smart_clinic_appointment_queue_optimization_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponseDto {
      private Long id;
      private Long doctorId;
      private String doctorName;
      private LocalDate date;
      private LocalTime startTime;
      private LocalTime endTime;
      private int maxSlots;
      private boolean isActive;
}
