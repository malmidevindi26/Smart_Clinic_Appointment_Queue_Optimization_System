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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

   private final DoctorRepository doctorRepository;
   private final PatientRepository patientRepository;
   private final AppointmentRepository appointmentRepository;
   private final ScheduleRepository scheduleRepository;
   private final PaymentService paymentService;

   @Transactional
   public AppointmentResponseDto bookAppointment(AppointmentRequestDto request) {

      Doctor doctor = doctorRepository.findById(request.getDoctorId())
              .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

      Patient patient = patientRepository.findById(request.getPatientId())
              .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

      Schedule schedule = scheduleRepository.findById(request.getScheduleId())
              .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

//      int currentAppointments = appointmentRepository.countByDoctorAndSchedule(doctor, schedule);
//
//      if (currentAppointments >= schedule.getMaxSlots()) {
//         throw new RuntimeException("No available slots for this schedule");
//      }

      //int queueNumber = currentAppointments + 1;
//      int currentActiveCount = appointmentRepository.countActiveAppointments(doctor, schedule);
//
//      if (currentActiveCount >= schedule.getMaxSlots()) {
//         throw new RuntimeException("No available slots. Capacity reached.");
//      }
//      int nextQueueNumber = currentActiveCount + 1;

      boolean alreadyBooked = appointmentRepository.existsByPatientIdAndScheduleIdAndStatus(
              request.getPatientId(),
              request.getScheduleId(),
              "BOOKED"
      );
      if (alreadyBooked) {
         throw new RuntimeException(" You already have an active appointment for this time slot.");
      }

      int activeAppointments = appointmentRepository.countActiveAppointments(doctor, schedule);

      if (activeAppointments >= schedule.getMaxSlots()) {
         throw new RuntimeException("Maximum patient limit (" + schedule.getMaxSlots() + ") reached for this schedule.");
      }

      // Assign the next available queue number
      int nextQueueNumber = activeAppointments + 1;

      Appointment appointment = Appointment.builder()
              .doctor(doctor)
              .patient(patient)
              .schedule(schedule)
              .appointmentDate(schedule.getDate())
              .queueNumber(nextQueueNumber)
              .status("BOOKED")
              .isPriority(request.isEmergency())
              .build();

      Appointment savedAppointment = appointmentRepository.save(appointment);

      double doctorFee = 2500.0;
      paymentService.processAppointmentPayment(savedAppointment, doctorFee, request.getPaymentMethod(), request.getTransactionId());

      return new AppointmentResponseDto(
              savedAppointment.getId(),
              doctor.getName(),
              patient.getName(),
              nextQueueNumber,
              savedAppointment.getAppointmentDate(),
              savedAppointment.getStatus(),
              savedAppointment.isPriority()
      );
   }
   @Transactional
   public String patientCancelAppointment(Long appointmentId, Long patientId) {
      Appointment appointment = appointmentRepository.findById(appointmentId)
              .orElseThrow(() -> new RuntimeException("Appointment not found"));

      if (!appointment.getPatient().getId().equals(patientId)) {
         throw new RuntimeException("Unauthorized: You can only cancel your own appointments.");
      }

      if(!"BOOKED".equals(appointment.getStatus())) {
         throw new RuntimeException("This appointment is already" + appointment.getStatus());
      }
      appointment.setStatus("CANCELLED");
      appointmentRepository.save(appointment);
      return "Appointment #" + appointmentId + " cancelled. Slot is now reallocated.";
   }

   @Transactional
   public String completeAppointment(Long appointmentId) {
      Appointment appointment = appointmentRepository.findById(appointmentId)
              .orElseThrow(() -> new RuntimeException("Appointment not found"));

      if(!"BOOKED".equals(appointment.getStatus())) {
         throw new RuntimeException("Only active bookings can be marked as completed. Current status: " + appointment.getStatus());
      }
         appointment.setStatus("COMPLETED");
         appointmentRepository.save(appointment);
         return "Appointment #" + appointmentId + " marked as COMPLETED. Patient visit history updated.";
      }

      public List<AppointmentResponseDto> getDoctorDailySchedule(Long doctorId) {
          Doctor doctor = doctorRepository.findById(doctorId)
                  .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

          LocalDate today = LocalDate.now();

          List<Appointment> dailyAppointment = appointmentRepository.findAllByDoctorAndAppointmentDateAndStatusOrderByIsPriorityDescQueueNumberAsc(doctor, today, "BOOKED");

          return dailyAppointment.stream().map(app -> new AppointmentResponseDto(
                  app.getId(),
                  app.getDoctor().getName(),
                  app.getPatient().getName(),
                  app.getQueueNumber(),
                  app.getAppointmentDate(),
                  app.getStatus(),
                  app.isPriority()
          )).toList();
      }

      public List<AppointmentResponseDto> getPatientHistory(Long patientId) {
          List<Appointment> history = appointmentRepository
                  .findAllByPatientIdAndStatusOrderByAppointmentDateDesc(patientId, "COMPLETED");

          return history.stream().map(app -> new AppointmentResponseDto(
                  app.getId(),
                  app.getDoctor().getName(),
                  app.getPatient().getName(),
                  app.getQueueNumber(),
                  app.getAppointmentDate(),
                  app.getStatus(),
                  app.isPriority()
          )).toList();
   }

   public List<AppointmentResponseDto> getDoctorDashboard(Long doctorId) {
      LocalDate today = LocalDate.now();

      return appointmentRepository.findAllByDoctorIdAndAppointmentDateAndStatusOrderByIsPriorityDescQueueNumberAsc(
              doctorId,
              today,
              "BOOKED"
      ).stream().map(app -> new AppointmentResponseDto(
              app.getId(),
              app.getDoctor().getName(),
              app.getPatient().getName(),
              app.getQueueNumber(),
              app.getAppointmentDate(),
              app.getStatus(),
              app.isPriority()
      )).toList();
   }

   public List<AppointmentResponseDto> getUpcomingAppointments(Long doctorId) {
      Doctor doctor = doctorRepository.findById(doctorId)
              .orElseThrow(() -> new RuntimeException("Doctor not found"));

      LocalDate today = LocalDate.now();

      return appointmentRepository.findAllByDoctorAndAppointmentDateGreaterThanEqualAndStatusOrderByAppointmentDateAscQueueNumberAsc(
              doctor,
              today,
              "BOOKED"
      ).stream().map(app -> new AppointmentResponseDto(
              app.getId(),
              app.getDoctor().getName(),
              app.getPatient().getName(),
              app.getQueueNumber(),
              app.getAppointmentDate(),
              app.getStatus(),
              app.isPriority()
      )).toList();
   }

   public List<AppointmentResponseDto> getPatientUpcomingAppointments(Long patientId) {
      LocalDate today = LocalDate.now();

      return appointmentRepository.findAllByPatientIdAndAppointmentDateGreaterThanEqualAndStatusOrderByAppointmentDateAsc(
              patientId,
              today,
              "BOOKED"
      ).stream().map(app -> new AppointmentResponseDto(
              app.getId(),
              app.getDoctor().getName(),
              app.getPatient().getName(),
              app.getQueueNumber(),
              app.getAppointmentDate(),
              app.getStatus(),
              app.isPriority()
      )).toList();
   }

   @Transactional
   public String doctorCancelAppointment(Long appointmentId) {
      Appointment appointment = appointmentRepository.findById(appointmentId)
              .orElseThrow(() -> new RuntimeException("Appointment not found"));

      if(!"BOOKED".equals(appointment.getStatus())) {
         throw new RuntimeException("Cannot cancel an appointment that is " + appointment.getStatus());
      }

      appointment.setStatus("CANCELLED");
      appointmentRepository.save(appointment);
      return "Appointment #" + appointmentId + " has been cancelled by the doctor.";
   }
}