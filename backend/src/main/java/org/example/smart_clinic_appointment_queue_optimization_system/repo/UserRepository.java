package org.example.smart_clinic_appointment_queue_optimization_system.repo;

import org.example.smart_clinic_appointment_queue_optimization_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

}
