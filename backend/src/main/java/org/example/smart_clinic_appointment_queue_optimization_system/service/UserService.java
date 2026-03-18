package org.example.smart_clinic_appointment_queue_optimization_system.service;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.AuthDto;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.AuthResponseDto;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.RegisterDto;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Doctor;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Patient;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Role;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.User;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.DoctorRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.PatientRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.UserRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.util.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

//    public String register(RegisterDto dto) {
//
//        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
//            throw new RuntimeException("Username already taken");
//        }
////        User user = User.builder()
////                .username(dto.getUsername())
////                .password(passwordEncoder.encode(dto.getPassword()))
////                .role(Role.valueOf(dto.getRole().toUpperCase()))
////                .createdAt(LocalDateTime.now())
////                .build();
////
////        User savedUser = userRepository.save(user);
////        // 3. Automated Profile Creation for Patients
////        if (savedUser.getRole() == Role.PATIENT) {
////            Patient patient = Patient.builder()
////                    .name(savedUser.getUsername()) // Use username as initial name
////                    .email(dto.getEmail()) // Assuming your RegisterDto has an email field
////                    .user(savedUser) // This links the two tables
////                    .build();
////            patientRepository.save(patient);
////        }
////
////        return "Registered Successfully. Profile created.";
//
//
//    }
@Transactional
public String registerPatient(RegisterDto dto) {
    User user = createUser(dto, Role.PATIENT);

    Patient patient = Patient.builder()
            .name(dto.getName())
            .phone(dto.getPhone())
            .email(dto.getEmail())
            .age(dto.getAge())
            .user(user)
            .build();
    patientRepository.save(patient);
    return "Patient account created successfully.";
}

    @Transactional
    public String addDoctor(RegisterDto dto) {
        User user = createUser(dto, Role.DOCTOR);

        Doctor doctor = Doctor.builder()
                .name(dto.getName())
                .specialization(dto.getSpecialization())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .dailyPatientLimit(dto.getDailyPatientLimit())
                .user(user)
                .build();
        doctorRepository.save(doctor);
        return "Doctor " + dto.getName() + " added to the system by Admin.";
    }

    private User createUser(RegisterDto dto, Role role) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    public AuthResponseDto login(AuthDto dto) throws Exception {

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Username not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
           throw new RuntimeException("Invalid password");
        }
        
        Long profileId = null;
        if (user.getRole() == Role.DOCTOR && user.getDoctor() != null) {
            profileId = user.getDoctor().getId();
        }else if(user.getRole() == Role.PATIENT && user.getPatient() != null) {
            profileId = user.getPatient().getId();
        } else if (user.getRole() == Role.ADMIN) {
            profileId = 0L;
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponseDto(token, profileId, user.getRole().name());
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}
