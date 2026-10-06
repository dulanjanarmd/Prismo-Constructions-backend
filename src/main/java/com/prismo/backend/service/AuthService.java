package com.prismo.backend.service;

import com.prismo.backend.dto.AuthRequest;
import com.prismo.backend.dto.AuthResponse;
import com.prismo.backend.dto.RegisterRequest;

import com.prismo.backend.model.Role;
import com.prismo.backend.model.User;
import com.prismo.backend.repository.UserRepository;
import com.prismo.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


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
                .phone(user.getPhone())
                .profilePictureUrl(user.getProfilePictureUrl())
                .nic(user.getNic())
                .address(user.getAddress())
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
                .phone(user.getPhone())
                .profilePictureUrl(user.getProfilePictureUrl())
                .nic(user.getNic())
                .address(user.getAddress())
                .build();
    }


}
