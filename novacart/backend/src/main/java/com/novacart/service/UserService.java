package com.novacart.service;

import com.novacart.model.User;
import com.novacart.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Business logic for user registration, login and management. */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Map<String, Object> register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return Map.of("success", false, "message", "Email already registered");
        }
        user.setRole("CUSTOMER");
        int id = userRepository.save(user);
        return Map.of("success", true, "message", "Account created successfully", "userId", id);
    }

    public Map<String, Object> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            return Map.of("success", false, "message", "Invalid email or password");
        }
        User user = userOpt.get();
        // Never send password back to client
        Map<String, Object> safeUser = Map.of(
            "id", user.getId(), "name", user.getName(), "email", user.getEmail(), "role", user.getRole()
        );
        return Map.of("success", true, "user", safeUser);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
