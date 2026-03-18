package org.example.smart_clinic_appointment_queue_optimization_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@SQLDelete(sql = "UPDATE doctor SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialization;
    private String phone;
    private String email;
    private int dailyPatientLimit;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    private List<Schedule> schedules;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;

    @Builder.Default
    private boolean isActive = true;
}
