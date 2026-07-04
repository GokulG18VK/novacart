package com.novacart.repository;

import com.novacart.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Direct JDBC data-access layer for the `users` table.
 * Uses Spring's JdbcTemplate (built on PreparedStatement) for safe, SQL-injection-proof queries.
 */
@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        return u;
    };

    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users ORDER BY id DESC", userRowMapper);
    }

    public Optional<User> findById(int id) {
        List<User> result = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", userRowMapper, id);
        return result.stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        List<User> result = jdbcTemplate.query("SELECT * FROM users WHERE email = ?", userRowMapper, email);
        return result.stream().findFirst();
    }

    /** Inserts a new user and returns the generated primary key. */
    public int save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO users (name, email, phone, password, role) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole() == null ? "CUSTOMER" : user.getRole());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email);
        return count != null && count > 0;
    }

    public void deleteById(int id) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }
}
