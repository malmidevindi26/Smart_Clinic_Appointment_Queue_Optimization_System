package org.example.smart_clinic_appointment_queue_optimization_system.service;

import lombok.RequiredArgsConstructor;
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
    public Appointment bookAppointment(Long doctorId, Long patientId, Long scheduleId) {
       Doctor doctor = doctorRepository.findById(doctorId)
               .orElseThrow(() -> new IllegalArgumentException("doctor not found"));

       Patient patient = patientRepository.findById(patientId)
               .orElseThrow(() -> new IllegalArgumentException("patient not found"));

       Schedule schedule = scheduleRepository.findById(scheduleId)
               .orElseThrow(() -> new IllegalArgumentException("schedule not found"));

       int currentAppointments = appointmentRepository.countByDoctorAndSchedule(doctor, schedule);

       int queueNumber = currentAppointments + 1;

       Appointment appointment =  Appointment.builder()
               .doctor(doctor)
               .patient(patient)
               .schedule(schedule)
               .appointmentDate(LocalDate.now())
               .queueNumber(queueNumber)
               .status("BOOKED")
               .build();

       return appointmentRepository.save(appointment);
   }
}
