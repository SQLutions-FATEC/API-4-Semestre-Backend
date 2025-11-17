package com.sqlutions.api_4_semestre_backend.controller;

import com.sqlutions.api_4_semestre_backend.entity.User;
import com.sqlutions.api_4_semestre_backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Endpoints for managing users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user or update an existing one")
    public User createUser(@RequestBody User user, @RequestHeader(value = "userId", required = false) Long userId) {
        User saveUser = userId != null ? userService.searchIdUser(userId) : null;
        return userService.saveUser(user, saveUser);
    }

    @GetMapping
    @Operation(summary = "List all users")
    public List<User> listUser() {
        return userService.listUser();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public User searchUser(@PathVariable Long id) {
        return userService.searchIdUser(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user by ID")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user by ID")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    public User login(@RequestBody User user) {
        return userService.login(user.getEmail(), user.getPassword());
    }
}
