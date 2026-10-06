package com.prismo.backend.controller;

import com.prismo.backend.dto.AuthRequest;
import com.prismo.backend.dto.AuthResponse;
import com.prismo.backend.dto.RegisterRequest;
import com.prismo.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
