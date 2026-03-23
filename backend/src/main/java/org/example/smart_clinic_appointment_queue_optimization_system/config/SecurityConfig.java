package org.example.smart_clinic_appointment_queue_optimization_system.config;

import lombok.RequiredArgsConstructor;
import org.example.smart_clinic_appointment_queue_optimization_system.util.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter  jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/v1/auth/register-patient", "/api/v1/auth/login").permitAll() // Public
//                        .requestMatchers("/api/v1/admin/add-doctor").hasRole("ADMIN") // Restricted
//                        .anyRequest().authenticated()
                                .requestMatchers("/api/v1/auth/**").permitAll()

                                // 1. Allow PATIENTS to see schedules so they can book!
                                // This was the main cause of your 403 error in openBooking()
                                .requestMatchers("/api/v1/admin/schedules").hasAnyRole("ADMIN", "PATIENT")

                                // 2. Allow PATIENTS to search and see doctors
                                .requestMatchers("/api/v1/doctors/all", "/api/v1/doctors/search").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")

                                // 3. APPOINTMENTS - Specific Patient access MUST come before the general /** rule
                                .requestMatchers("/api/v1/appointments/book").hasRole("PATIENT")
                                .requestMatchers("/api/v1/appointments/patient-history/**").hasRole("PATIENT")
                                .requestMatchers("/api/v1/appointments/patient/cancel/**").hasRole("PATIENT")

                                // 4. General Appointment access for staff
                                .requestMatchers("/api/v1/appointments/**").hasAnyRole("ADMIN", "DOCTOR")

                                // 5. Patient profile access
                                .requestMatchers("/api/v1/patients/**").hasRole("PATIENT")

                                // 6. Restrict other admin routes
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Use setAllowedOriginPatterns for broad matching that supports credentials
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    }

