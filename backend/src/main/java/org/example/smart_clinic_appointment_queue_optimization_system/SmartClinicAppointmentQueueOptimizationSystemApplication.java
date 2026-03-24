package org.example.smart_clinic_appointment_queue_optimization_system;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartClinicAppointmentQueueOptimizationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartClinicAppointmentQueueOptimizationSystemApplication.class, args);
    }
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

}
