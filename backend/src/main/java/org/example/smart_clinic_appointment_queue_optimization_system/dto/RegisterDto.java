package org.example.smart_clinic_appointment_queue_optimization_system.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String password;
    private String role;

    private String name;
    private String phone;
    private String email;

    private int age;

    private String specialization;
    private int dailyPatientLimit;
}
