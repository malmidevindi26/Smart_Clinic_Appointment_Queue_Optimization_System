package org.example.smart_clinic_appointment_queue_optimization_system.service;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.RegisterDto;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Doctor;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Schedule;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.DoctorRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.ScheduleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository  doctorRepository;
    private final ScheduleRepository scheduleRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> searchBySpecialization(String specialization) {
        if (specialization == null || specialization.isEmpty()) {
            return doctorRepository.findAll();
        }

        return doctorRepository.findBySpecializationContainingIgnoreCase(specialization);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deactivateDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("doctor not found"));

        doctor.setActive(false);
        doctorRepository.save(doctor);

        List<Schedule> schedules = scheduleRepository.findByDoctorId(doctorId);
        schedules.forEach(s -> s.setActive(false));
        scheduleRepository.saveAll(schedules);

        return "Doctor and their schedules have been deactivated";
    }

    @Transactional
    public String updateDoctor(Long id, RegisterDto dto){
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("doctor not found"));

        doctor.setName(dto.getName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setPhone(dto.getPhone());
        doctor.setEmail(dto.getEmail());
        doctor.setDailyPatientLimit(dto.getDailyPatientLimit());

        doctorRepository.save(doctor);
        return "Doctor " + doctor.getName() + " has been updated";
    }
}
