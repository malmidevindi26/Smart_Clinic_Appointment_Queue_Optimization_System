package org.example.smart_clinic_appointment_queue_optimization_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForecastReportDto {

    private LocalDateTime generatedAt;
    private int lookbackDays;
    private int totalDoctorsAnalyzed;

    private String busiestPredictedDoctor;
    private double busiestPredictedCount;

    private String summary;

    private List<DoctorForecastDto> forecasts;
}