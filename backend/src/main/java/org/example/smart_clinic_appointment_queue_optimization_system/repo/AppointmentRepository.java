package org.example.smart_clinic_appointment_queue_optimization_system.repo;

import org.example.smart_clinic_appointment_queue_optimization_system.entity.Appointment;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Doctor;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
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
    List<Appointment> findAllByDoctorIdAndAppointmentDateAndStatusOrderByIsPriorityDescQueueNumberAsc(
            Long doctorId, LocalDate date, String status);

    List<Appointment> findAllByDoctorAndAppointmentDateAndStatusOrderByIsPriorityDescQueueNumberAsc(
            Doctor doctor, LocalDate date, String status);

    List<Appointment> findAllByAppointmentDateBeforeAndStatus(LocalDate date, String status);

    @Modifying
    @Query("UPDATE Appointment a SET a.status = 'CANCELLED' " +
            "WHERE a.status = 'BOOKED' " +
            "AND (a.appointmentDate < :today OR (a.appointmentDate = :today AND a.schedule.endTime < :now))")
    int markExpiredAsCancelled(@Param("today") LocalDate today, @Param("now") LocalTime now);

    List<Appointment> findAllByStatusInOrderByAppointmentDateDesc(List<String> statuses);

    @Query("SELECT a.doctor.id, a.appointmentDate, COUNT(a) " +
            "FROM Appointment a " +
            "WHERE a.status != 'CANCELLED' AND a.appointmentDate >= :fromDate " +
            "GROUP BY a.doctor.id, a.appointmentDate " +
            "ORDER BY a.doctor.id, a.appointmentDate")
    List<Object[]> findDailyAppointmentCountsPerDoctor(@Param("fromDate") LocalDate fromDate);
}
