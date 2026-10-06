package com.prismo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private Long id;
    private String name;
    private String email;
    private String role;
    private String phone;
    private String profilePictureUrl;
    private String nic;
    private String address;
}
