package org.example.smart_clinic_appointment_queue_optimization_system.controller;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ApiResponse;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ScheduleRequestDto;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Schedule;
import org.example.smart_clinic_appointment_queue_optimization_system.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleController {
    private final ScheduleService  scheduleService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addSchedule(@RequestBody ScheduleRequestDto dto) {
        return ResponseEntity.ok(
                new ApiResponse(200, "Success", scheduleService.createSchedule(dto))
        );
    }

@GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(new ApiResponse(200, "Success", scheduleService.getSchedulesByDoctor(doctorId)));
}

    @GetMapping("/available")
    public ResponseEntity<ApiResponse> getAvailableSchedules() {
        return ResponseEntity.ok(
                new ApiResponse(200, "Success", scheduleService.getAvailableSchedulesForPatients())
        );
    }
}
