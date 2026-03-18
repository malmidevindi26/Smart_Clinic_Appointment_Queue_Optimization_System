package org.example.smart_clinic_appointment_queue_optimization_system.controller;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ApiResponse;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.RegisterDto;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ScheduleRequestDto;
import org.example.smart_clinic_appointment_queue_optimization_system.service.DoctorService;
import org.example.smart_clinic_appointment_queue_optimization_system.service.ScheduleService;
import org.example.smart_clinic_appointment_queue_optimization_system.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserService userService;
    private final DoctorService doctorService;
    private final ScheduleService scheduleService;

    @PostMapping("/add-doctor")
    public ResponseEntity<ApiResponse> addDoctor(@RequestBody RegisterDto dto) {
        try {
            return ResponseEntity.ok(new ApiResponse(200, "Success", userService.addDoctor(dto)));
        }catch (RuntimeException e) {
            // Return a 400 Bad Request with the specific error message
            return ResponseEntity.status(400).body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @DeleteMapping("/doctor/{id}")
    public ResponseEntity<ApiResponse> deleteDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse(
                200, "Success", doctorService.deactivateDoctor(id)
        ));
    }

    @DeleteMapping("/schedule/{id}")
    public ResponseEntity<ApiResponse> deleteSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse(200, "Success", scheduleService.deactivateSchedule(id)));
    }

    @PutMapping("/doctor/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateDoctorProfile(@PathVariable Long id, @RequestBody RegisterDto dto){
        return ResponseEntity.ok(new ApiResponse(200, "Doctor profile updated", doctorService.updateDoctor(id, dto)));
    }

    @PutMapping("/schedule/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDto dto){
        return ResponseEntity.ok(new ApiResponse(200, "Schedule updated", scheduleService.updateSchedule(id, dto)));
    }

    @GetMapping("/schedules")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllSchedules() {
        return ResponseEntity.ok(
                new ApiResponse(200, "Success", scheduleService.getAllSchedules())
        );
    }
}
