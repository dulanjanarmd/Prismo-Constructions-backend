package com.prismo.backend.controller;

import com.prismo.backend.dto.CreateUserRequest;
import com.prismo.backend.dto.UpdateUserRequest;
import com.prismo.backend.dto.PasswordResetRequest;
import com.prismo.backend.model.User;
import com.prismo.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('CEO')")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(adminService.createUser(request));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(adminService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deactivateUser(id));
    }

    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.activateUser(id));
    }

    @PatchMapping("/users/{id}/suspend")
    public ResponseEntity<User> suspendUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.suspendUser(id));
    }

    @PatchMapping("/users/{id}/password")
    public ResponseEntity<String> resetUserPassword(@PathVariable Long id, @RequestBody PasswordResetRequest request) {
        adminService.resetUserPassword(id, request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }
}
