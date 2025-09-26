package com.sqlutions.api_4_semestre_backend.controller;

import com.sqlutions.api_4_semestre_backend.entity.User;
import com.sqlutions.api_4_semestre_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user, @RequestHeader(value = "userId", required = false) Long userId) {
        User saveUser = userId != null ? userService.searchIdUser(userId) : null;
        return userService.saveUser(user, saveUser);
    }

    @GetMapping
    public List<User> listUser() {
        return userService.listUser();
    }

    @GetMapping("/{id}")
    public User searchUser(@PathVariable Long id) {
        return userService.searchIdUser(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {
        return userService.login(user.getEmail(), user.getPassword());
    }
}

    



