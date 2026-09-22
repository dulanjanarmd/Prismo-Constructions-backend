package com.prismo.backend.controller;

import com.prismo.backend.model.User;
import com.prismo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(Authentication authentication, @RequestBody Map<String, String> updates) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        if (updates.containsKey("name")) user.setName(updates.get("name"));
        if (updates.containsKey("phone")) user.setPhone(updates.get("phone"));
        if (updates.containsKey("profilePictureUrl")) user.setProfilePictureUrl(updates.get("profilePictureUrl"));

        if (updates.containsKey("currentPassword") && updates.containsKey("newPassword")) {
            if (passwordEncoder.matches(updates.get("currentPassword"), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(updates.get("newPassword")));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Incorrect current password"));
            }
        }

        userRepository.save(user);
        
        // Return updated user data (excluding password)
        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail(),
            "role", user.getRole(),
            "phone", user.getPhone() != null ? user.getPhone() : "",
            "profilePictureUrl", user.getProfilePictureUrl() != null ? user.getProfilePictureUrl() : ""
        ));
    }
}
