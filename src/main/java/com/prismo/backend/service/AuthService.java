package com.prismo.backend.service;

import com.prismo.backend.dto.AuthRequest;
import com.prismo.backend.dto.AuthResponse;
import com.prismo.backend.dto.RegisterRequest;
import com.prismo.backend.dto.ResetPasswordRequest;
import com.prismo.backend.dto.ForgotPasswordRequest;
import com.prismo.backend.model.Role;
import com.prismo.backend.model.User;
import com.prismo.backend.repository.UserRepository;
import com.prismo.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest request) {
        if (request.getRole() != Role.CLIENT) {
            throw new RuntimeException("Only CLIENT accounts can be created via public registration. Please contact admin for other roles.");
        }
        
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(com.prismo.backend.model.UserStatus.ACTIVE)
                .build();
        repository.save(user);
        
        var jwtToken = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));
        
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setResetOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
        repository.save(user);

        emailService.sendPasswordResetOtp(user.getEmail(), otp);
    }

    public void resetPassword(ResetPasswordRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));
        
        if (user.getResetOtp() == null || !user.getResetOtp().equals(request.getOtp())) {
            throw new IllegalArgumentException("Invalid OTP");
        }
        
        if (user.getOtpExpiryTime() != null && user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetOtp(null);
        user.setOtpExpiryTime(null);
        repository.save(user);
    }
}
