package org.example.smart_clinic_appointment_queue_optimization_system.repo;

import org.example.smart_clinic_appointment_queue_optimization_system.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByDoctorId(Long doctorId);

    List<Schedule> findByDate(LocalDate date);

    @Query("SELECT s FROM Schedule s WHERE s.doctor.id = :doctorId AND s.date = :date " +
            "AND s.isActive = true " +
            "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<Schedule> findOverlappingSchedules(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime);

    @Query("SELECT s FROM Schedule s WHERE s.doctor.id = :doctorId AND s.date = :date " +
            "AND s.id != :excludeId AND s.isActive = true " +
            "AND (s.startTime < :endTime AND s.endTime > :startTime)")
    List<Schedule> findOverlappingSchedulesExcludingId(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime, Long excludeId);

    @Query("SELECT s FROM Schedule s WHERE s.isActive = true " +
            "AND (s.date > CURRENT_DATE OR (s.date = CURRENT_DATE AND s.startTime > CURRENT_TIME))")
    List<Schedule> findAvailableSchedulesForPatients();
}
