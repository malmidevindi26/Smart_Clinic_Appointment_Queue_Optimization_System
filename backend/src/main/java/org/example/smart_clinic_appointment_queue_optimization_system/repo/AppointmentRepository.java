package org.example.smart_clinic_appointment_queue_optimization_system.repo;

import org.example.smart_clinic_appointment_queue_optimization_system.entity.Appointment;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Doctor;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    int countByDoctorAndSchedule(Doctor doctor, Schedule schedule);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor " +
            "AND a.schedule = :schedule AND a.status = 'BOOKED'")
    int countActiveAppointments(@Param("doctor") Doctor doctor, @Param("schedule") Schedule schedule);

    List<Appointment> findAllByDoctorAndAppointmentDateAndStatusOrderByQueueNumberAsc(
            Doctor doctor,
            LocalDate date,
            String status
    );

    List<Appointment> findAllByPatientIdAndStatusOrderByAppointmentDateDesc(
            Long patientId,
            String status
    );

    List<Appointment> findAllByDoctorIdAndAppointmentDateAndStatusOrderByQueueNumberAsc(
            Long doctorId,
            LocalDate date,
            String status
    );
    List<Appointment> findAllByDoctorAndAppointmentDateGreaterThanEqualAndStatusOrderByAppointmentDateAscQueueNumberAsc(
            Doctor doctor,
            LocalDate date,
            String status
    );

    List<Appointment>findAllByPatientIdAndAppointmentDateGreaterThanEqualAndStatusOrderByAppointmentDateAsc(
            Long patientId,
            LocalDate date,
            String status
    );

    boolean existsByPatientIdAndScheduleIdAndStatus(
            Long patientId,
            Long scheduleId,
            String status
    );

    int countByScheduleIdAndStatus(Long scheduleId, String status);
}
