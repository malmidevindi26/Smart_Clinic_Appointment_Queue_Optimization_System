package org.example.smart_clinic_appointment_queue_optimization_system.controller;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ApiResponse;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.DoctorRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.service.AppointmentService;
import org.example.smart_clinic_appointment_queue_optimization_system.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAll(){
        return ResponseEntity.ok(new ApiResponse(200, "Success", doctorService.getAllDoctors()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> search(@RequestParam(value = "q", required = false) String q) {
        return ResponseEntity.ok(
                new ApiResponse(200, "Doctor found", doctorService.searchBySpecialization(q))
        );
    }

    @GetMapping("/{doctorId}/today-appointments")
    public ResponseEntity<ApiResponse> getTodayAppointments(@PathVariable("doctorId") Long doctorId) {
        return ResponseEntity.ok(
                new ApiResponse(200, "Today's queue retrieved", appointmentService.getDoctorDashboard(doctorId))
        );
    }

    @GetMapping("/{doctorId}/upcoming")
    public ResponseEntity<ApiResponse> getUpcoming(@PathVariable Long doctorId) {
        return ResponseEntity.ok(
                new ApiResponse(200, "Upcoming appointments retrieved", appointmentService.getUpcomingAppointments(doctorId))
        );
    }
}
