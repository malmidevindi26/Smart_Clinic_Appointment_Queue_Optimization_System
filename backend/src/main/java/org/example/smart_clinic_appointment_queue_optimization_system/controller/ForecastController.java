package org.example.smart_clinic_appointment_queue_optimization_system.controller;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ApiResponse;
import org.example.smart_clinic_appointment_queue_optimization_system.service.ForecastService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/forecast")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ForecastController {

    private final ForecastService forecastService;

    @GetMapping("/doctors")
    public ResponseEntity<ApiResponse> forecastDoctors(@RequestParam(defaultValue = "30") int lookbackDays){
        return ResponseEntity.ok(
                new ApiResponse(200, "Success", forecastService.forecastNextPeriod(lookbackDays))
        );
    }

    @GetMapping("/report")
    public ResponseEntity<ApiResponse> forecastReport(@RequestParam(defaultValue = "30") int lookbackDays){
        return ResponseEntity.ok(
                new ApiResponse(200, "Success", forecastService.generateReport(lookbackDays))
        );
    }
}
