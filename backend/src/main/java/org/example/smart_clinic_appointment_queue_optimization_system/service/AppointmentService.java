package org.example.smart_clinic_appointment_queue_optimization_system.service;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.AppointmentRequestDto;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.AppointmentResponseDto;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Appointment;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Doctor;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Patient;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Schedule;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.AppointmentRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.DoctorRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.PatientRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AppointmentService {

   private final DoctorRepository doctorRepository;
   private final PatientRepository patientRepository;
   private final AppointmentRepository appointmentRepository;
   private final ScheduleRepository scheduleRepository;

   @Transactional
   public AppointmentResponseDto bookAppointment(AppointmentRequestDto request) {

      Doctor doctor = doctorRepository.findById(request.getDoctorId())
              .orElseThrow(() -> new IllegalArgumentException("doctor not found"));

      Patient patient = patientRepository.findById(request.getPatientId())
              .orElseThrow(() -> new IllegalArgumentException("patient not found"));

      Schedule schedule = scheduleRepository.findById(request.getScheduleId())
              .orElseThrow(() -> new IllegalArgumentException("schedule not found"));

      int currentAppointments =
              appointmentRepository.countByDoctorAndSchedule(doctor, schedule);

      int queueNumber = currentAppointments + 1;

      if (queueNumber > schedule.getMaxSlots()) {
         throw new RuntimeException("No available slots for this schedule");
      }

      Appointment appointment = Appointment.builder()
              .doctor(doctor)
              .patient(patient)
              .schedule(schedule)
              .appointmentDate(LocalDate.now())
              .queueNumber(queueNumber)
              .status("BOOKED")
              .build();

      appointmentRepository.save(appointment);

      return new AppointmentResponseDto(
              appointment.getId(),
              doctor.getName(),
              patient.getName(),
              queueNumber,
              appointment.getAppointmentDate(),
              appointment.getStatus()
      );
   }
}