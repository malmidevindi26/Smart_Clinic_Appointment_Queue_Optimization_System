package org.example.smart_clinic_appointment_queue_optimization_system.repo;

import org.example.smart_clinic_appointment_queue_optimization_system.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
   List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);
}
