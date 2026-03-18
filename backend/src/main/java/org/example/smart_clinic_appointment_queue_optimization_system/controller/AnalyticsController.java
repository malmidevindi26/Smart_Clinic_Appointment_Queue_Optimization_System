package org.example.smart_clinic_appointment_queue_optimization_system.controller;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ApiResponse;
import org.example.smart_clinic_appointment_queue_optimization_system.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticsController {
    private final AnalyticsService analyticsService; // Inject Service instead of Repo

    @GetMapping("/revenue/total")
    public ResponseEntity<ApiResponse> getTotalRevenue() {
        return ResponseEntity.ok(
                new ApiResponse(200, "Success", analyticsService.getTotalRevenue())
        );
    }

    @GetMapping("/revenue/doctor/{doctorId}")
    public ResponseEntity<ApiResponse> getDoctorRevenue(@PathVariable Long doctorId) {
        return ResponseEntity.ok(
                new ApiResponse(200, "Success", analyticsService.getDoctorRevenue(doctorId))
        );
    }
    @GetMapping("/revenue/today")
    public ResponseEntity<ApiResponse> getTodayRevenue() {
        return ResponseEntity.ok(
                new ApiResponse(200, "Success", analyticsService.getTodayRevenue())
        );
    }
}
