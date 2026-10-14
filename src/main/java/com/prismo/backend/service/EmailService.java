package com.prismo.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetOtp(String to, String otp) {
        log.info("Sending OTP {} to {}", otp, to);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Prismo Constructions - Password Reset OTP");
            message.setText("Your One-Time Password (OTP) for resetting your password is: " + otp + "\n\nThis code will expire in 10 minutes.");
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Failed to send email to {} (Dummy credentials?): {}", to, e.getMessage());
        }
    }

    public void sendLoginMfaCode(String to, String otp) {
        log.info("Sending Login MFA OTP {} to {}", otp, to);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Prismo Constructions - Login Verification Code");
            message.setText("Your verification code for login is: " + otp + "\n\nThis code will expire in 10 minutes.");
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Failed to send MFA email to {}: {}", to, e.getMessage());
        }
    }
}
