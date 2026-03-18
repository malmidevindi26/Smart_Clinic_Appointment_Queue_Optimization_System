package org.example.smart_clinic_appointment_queue_optimization_system.controller;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ApiResponse;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.AppointmentRequestDto;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.AppointmentResponseDto;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Appointment;
import org.example.smart_clinic_appointment_queue_optimization_system.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public AppointmentResponseDto bookAppointment(@RequestBody AppointmentRequestDto request){
        return appointmentService.bookAppointment(request);
    }
    @PutMapping("/cancel/{id}")
    public ApiResponse cancelAppointment(@PathVariable("id") long id){
        return new ApiResponse(200, "success", appointmentService.cancelAppointment(id));
    }

    @PutMapping("/complete/{id}")
    public ApiResponse completeAppointment(@PathVariable("id") long id){
        return new ApiResponse(200, "success", appointmentService.completeAppointment(id));
    }

    @GetMapping("/doctor-schedule/{doctorId}")
    public ResponseEntity<ApiResponse> getDailySchedule(@PathVariable long doctorId){
        return ResponseEntity.ok(
                new ApiResponse(200,"Success", appointmentService.getDoctorDailySchedule(doctorId))
        );
    }
    @GetMapping("/patient-history/{patientId}")
    public ResponseEntity<ApiResponse> getPatientHistory(@PathVariable long patientId){
        return ResponseEntity.ok(
                new ApiResponse(200,"Success", appointmentService.getPatientHistory(patientId)));
    }
}
