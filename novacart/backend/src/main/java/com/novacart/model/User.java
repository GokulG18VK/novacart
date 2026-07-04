package com.novacart.model;

import java.time.LocalDateTime;

/** Represents a row in the `users` table. */
public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password; // NOTE: hash in production (e.g. BCrypt)
    private String role;     // CUSTOMER or ADMIN
    private LocalDateTime createdAt;

    public User() {}

    public User(int id, String name, String email, String phone, String password, String role, LocalDateTime createdAt) {
        this.id = id; this.name = name; this.email = email; this.phone = phone;
        this.password = password; this.role = role; this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
