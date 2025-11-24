package com.sqlutions.api_4_semestre_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.dto.LoginRequestDto;
import com.sqlutions.api_4_semestre_backend.dto.LoginResponseDto;
import com.sqlutions.api_4_semestre_backend.entity.User;
import com.sqlutions.api_4_semestre_backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Endpoints for managing users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user or update an existing one", 
              security = @SecurityRequirement(name = "Bearer Authentication"))
    @PreAuthorize("hasRole('GESTOR')")
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user, null);
    }

    @GetMapping
    @Operation(summary = "List all users",
              security = @SecurityRequirement(name = "Bearer Authentication"))
    @PreAuthorize("hasRole('GESTOR')")
    public List<User> listUser() {
        return userService.listUser();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID",
              security = @SecurityRequirement(name = "Bearer Authentication"))
    @PreAuthorize("hasRole('GESTOR')")
    public User searchUser(@PathVariable Long id) {
        return userService.searchIdUser(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user by ID",
              security = @SecurityRequirement(name = "Bearer Authentication"))
    @PreAuthorize("hasRole('GESTOR')")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user by ID",
              security = @SecurityRequirement(name = "Bearer Authentication"))
    @PreAuthorize("hasRole('GESTOR')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto response = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(response);
    }
}
