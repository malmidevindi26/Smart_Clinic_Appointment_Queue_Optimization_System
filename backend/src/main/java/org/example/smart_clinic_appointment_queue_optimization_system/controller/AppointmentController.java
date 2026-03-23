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
    public ResponseEntity<ApiResponse> bookAppointment(@RequestBody AppointmentRequestDto request){
        return ResponseEntity.ok(
                new ApiResponse(200, "Appointment booked successfully", appointmentService.bookAppointment(request))
        );
    }
    @PutMapping("/patient/cancel/{id}")
    public ResponseEntity<ApiResponse> patientCancel(@PathVariable Long id,@RequestParam Long patientId){
        return ResponseEntity.ok(
                new ApiResponse(200, "Success", appointmentService.patientCancelAppointment(id, patientId))
        );    }

    @PutMapping("/complete/{id}")
    public ApiResponse completeAppointment(@PathVariable("id") long id){
        return new ApiResponse(200, "Appointment marked as completed", appointmentService.completeAppointment(id));
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
//    @PutMapping("/{type}/{id}")
//    public ResponseEntity<ApiResponse> updateStatus(
//            @PathVariable String type,
//            @PathVariable Long id
//    ) {
//
//        return ResponseEntity.ok(new ApiResponse(200, "Status updated", null));
//    }
}
