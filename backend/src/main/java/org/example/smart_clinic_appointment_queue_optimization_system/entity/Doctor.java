package org.example.smart_clinic_appointment_queue_optimization_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialization;
    private String phone;
    private String email;
    private String dailyPatientLimit;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    private List<Schedule> schedules;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;
}
