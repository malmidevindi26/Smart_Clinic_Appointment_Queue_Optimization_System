package org.example.smart_clinic_appointment_queue_optimization_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmergencyAlert(String doctorEmail, String doctorName, String patientName, int queueNo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(doctorEmail);
        message.setSubject("🚨 URGENT: Emergency Appointment Alert");
        message.setText("Dear Dr. " + doctorName + ",\n\n" +
                "This is an automated alert from Smart Clinic.\n" +
                "An EMERGENCY appointment has just been booked.\n\n" +
                "Patient: " + patientName + "\n" +
                "Queue Number: #" + queueNo + "\n" +
                "Please check your dashboard immediately.\n\n" +
                "Regards,\nSmart Clinic System");

        mailSender.send(message);
    }
}
