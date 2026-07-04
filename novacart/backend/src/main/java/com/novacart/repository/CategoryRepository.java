package com.novacart.repository;

import com.novacart.model.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Direct JDBC data-access layer for the `categories` table. */
@Repository
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Category> categoryRowMapper = (rs, rowNum) ->
        new Category(rs.getInt("id"), rs.getString("name"), rs.getString("image_url"));

    public List<Category> findAll() {
        return jdbcTemplate.query("SELECT * FROM categories ORDER BY id ASC", categoryRowMapper);
    }
}
