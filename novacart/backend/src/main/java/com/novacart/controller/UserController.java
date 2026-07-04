package com.novacart.controller;

import com.novacart.model.User;
import com.novacart.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST endpoints for user registration, login and admin user management.
 * Base path: /api/users
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        return userService.login(credentials.get("email"), credentials.get("password"));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return Map.of("success", true, "message", "User deleted");
    }
}
