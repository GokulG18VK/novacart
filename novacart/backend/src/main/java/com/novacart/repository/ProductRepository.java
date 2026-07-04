package com.novacart.repository;

import com.novacart.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/** Direct JDBC data-access layer for the `products` table (joined with categories). */
@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String BASE_SELECT =
        "SELECT p.*, c.name AS category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id ";

    private final RowMapper<Product> productRowMapper = (rs, rowNum) -> {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setCategoryId((Integer) rs.getObject("category_id"));
        p.setCategoryName(rs.getString("category_name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setOldPrice(rs.getBigDecimal("old_price"));
        p.setImageUrl(rs.getString("image_url"));
        p.setRating(rs.getBigDecimal("rating"));
        p.setReviewsCount(rs.getInt("reviews_count"));
        p.setBadge(rs.getString("badge"));
        p.setStock(rs.getInt("stock"));
        return p;
    };

    public List<Product> findAll() {
        return jdbcTemplate.query(BASE_SELECT + "ORDER BY p.id DESC", productRowMapper);
    }

    public Optional<Product> findById(int id) {
        List<Product> result = jdbcTemplate.query(BASE_SELECT + "WHERE p.id = ?", productRowMapper, id);
        return result.stream().findFirst();
    }

    public List<Product> findByCategory(String categoryName) {
        return jdbcTemplate.query(BASE_SELECT + "WHERE c.name = ?", productRowMapper, categoryName);
    }

    public List<Product> search(String keyword) {
        return jdbcTemplate.query(BASE_SELECT + "WHERE p.name LIKE ?", productRowMapper, "%" + keyword + "%");
    }

    public List<Product> filter(String category, Double maxPrice, Double minRating) {
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE 1=1 ");
        List<Object> params = new java.util.ArrayList<>();
        if (category != null && !category.isEmpty()) {
            sql.append("AND c.name = ? ");
            params.add(category);
        }
        if (maxPrice != null) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
        }
        if (minRating != null) {
            sql.append("AND p.rating >= ? ");
            params.add(minRating);
        }
        return jdbcTemplate.query(sql.toString(), productRowMapper, params.toArray());
    }

    public int save(Product product) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO products (name, category_id, description, price, old_price, image_url, rating, reviews_count, badge, stock) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, product.getName());
            ps.setObject(2, product.getCategoryId());
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            ps.setBigDecimal(5, product.getOldPrice());
            ps.setString(6, product.getImageUrl());
            ps.setBigDecimal(7, product.getRating());
            ps.setInt(8, product.getReviewsCount());
            ps.setString(9, product.getBadge());
            ps.setInt(10, product.getStock());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void update(int id, Product product) {
      jdbcTemplate.update(
           "UPDATE products SET name=?, category_id=?, description=?, price=?, old_price=?, image_url=?, rating=?, badge=?, stock=? WHERE id=?",
           product.getName(), product.getCategoryId(), product.getDescription(), product.getPrice(),
           product.getOldPrice(), product.getImageUrl(), product.getRating(), product.getBadge(), product.getStock(), id
          );
    }

    public void deleteById(int id) {
        jdbcTemplate.update("DELETE FROM products WHERE id = ?", id);
    }
}
