package org.example.smart_clinic_appointment_queue_optimization_system.controller;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.ApiResponse;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.AuthDto;
import org.example.smart_clinic_appointment_queue_optimization_system.dto.RegisterDto;
import org.example.smart_clinic_appointment_queue_optimization_system.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterDto dto){
        return ResponseEntity.ok(
                new ApiResponse(200, "Success", userService.register(dto))
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody AuthDto dto) throws Exception {
        return ResponseEntity.ok(
                new ApiResponse(200, "Success", userService.login(dto))
        );
    }
}
