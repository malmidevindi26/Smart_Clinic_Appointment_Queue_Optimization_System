package org.example.smart_clinic_appointment_queue_optimization_system.service;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ScheduleRequestDto;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ScheduleResponseDto;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Doctor;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Schedule;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.AppointmentRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.DoctorRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.ScheduleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public String createSchedule(ScheduleRequestDto dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("doctor not found"));

        List<Schedule> conflict = scheduleRepository.findOverlappingSchedules(
                dto.getDoctorId(),dto.getDate(),dto.getStartTime(),dto.getEndTime()
        );

        if (!conflict.isEmpty()) {
            throw new RuntimeException("Conflict: Dr. " + doctor.getName() +
                    "already has a schedule during this time period (" + conflict.get(0)
                    .getStartTime() + "-" + conflict.get(0).getEndTime() + ")");
        }

        if (dto.getMaxSlots() > doctor.getDailyPatientLimit()){
            throw new RuntimeException("Max slots exceeds doctor's daily  limit of " + doctor.getDailyPatientLimit());
        }
        Schedule schedule = Schedule.builder()
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .maxSlots(dto.getMaxSlots())
                .doctor(doctor)
                .isActive(true)
                .build();

        scheduleRepository.save(schedule);
                return "Schedule created for Dr. " + doctor.getName();

    }


    public List<Schedule> getSchedulesByDoctor(Long doctorId) {
        return scheduleRepository.findByDoctorId(doctorId);
    }

    public List<Schedule> getSchedulesByDate(LocalDate date) {
        return scheduleRepository.findByDate(date);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deactivateSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        schedule.setActive(false);
        scheduleRepository.save(schedule);
        return "Schedule deactivated successfully";
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public String updateSchedule(Long id,  ScheduleRequestDto dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        List<Schedule> conflicts = scheduleRepository.findOverlappingSchedulesExcludingId(
                schedule.getDoctor().getId(), dto.getDate(), dto.getStartTime(), dto.getEndTime(), id);

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Update Failed: Overlaps with an existing slot (" +
                    conflicts.get(0).getStartTime() + " - " + conflicts.get(0).getEndTime() + ")");
        }

        if (dto.getMaxSlots() > schedule.getDoctor().getDailyPatientLimit()) {
            throw new RuntimeException("Max slots exceed the doctor's daily limit of " +
                    schedule.getDoctor().getDailyPatientLimit());
        }

        int currentBooking = appointmentRepository.countByScheduleIdAndStatus(id, "BOOKED");

        if (dto.getMaxSlots() < currentBooking){
            throw  new RuntimeException("max slots is less than current booking");
        }

        schedule.setDate(dto.getDate());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setMaxSlots(dto.getMaxSlots());

        scheduleRepository.save(schedule);
        return "Schedule updated successfully" + dto.getDate();
    }



    public List<ScheduleResponseDto> getAllSchedules() {
        return  scheduleRepository.findAll().stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleResponseDto.class))
                .collect(Collectors.toList());

    }
}
