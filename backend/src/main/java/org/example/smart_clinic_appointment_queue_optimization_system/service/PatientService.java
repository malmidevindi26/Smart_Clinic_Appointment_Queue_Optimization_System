package org.example.smart_clinic_appointment_queue_optimization_system.service;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Patient;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.DoctorRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.PatientRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
   private final PatientRepository patientRepository;

   public Patient getPatientProfile(Long id){
       return patientRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Patient not found"));
   }
}
