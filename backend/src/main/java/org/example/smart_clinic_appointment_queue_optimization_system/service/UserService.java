package org.example.smart_clinic_appointment_queue_optimization_system.service;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.AuthDto;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.AuthResponseDto;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.RegisterDto;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.Role;
import org.example.smart_clinic_appointment_queue_optimization_system.entity.User;
import org.example.smart_clinic_appointment_queue_optimization_system.repo.UserRepository;
import org.example.smart_clinic_appointment_queue_optimization_system.util.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterDto dto) {

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.valueOf(dto.getRole().toUpperCase()))
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        return "Registered Successfully";
    }
    public AuthResponseDto login(AuthDto dto) throws Exception {

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Username not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
           throw new RuntimeException("Invalid password");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponseDto(token);
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
