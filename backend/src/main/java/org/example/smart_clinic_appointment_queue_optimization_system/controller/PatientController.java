package org.example.smart_clinic_appointment_queue_optimization_system.controller;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ApiResponse;
import org.example.smart_clinic_appointment_queue_optimization_system.service.AppointmentService;
import org.example.smart_clinic_appointment_queue_optimization_system.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {
    private final PatientService  patientService;
    private final AppointmentService appointmentService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse(200, "Success", patientService.getPatientProfile(id)));
    }

    @GetMapping("/{patientId}/upcoming")
    public ResponseEntity<ApiResponse> getUpcoming(@PathVariable Long patientId) {
        return  ResponseEntity.ok(
                new ApiResponse(200, "Patient upcoming appointment retrieved", appointmentService.getPatientUpcomingAppointments(patientId))
        );
    }
}
