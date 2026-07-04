package com.novacart.repository;

import com.novacart.model.WishlistItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Direct JDBC data-access layer for the `wishlist` table (joined with products). */
@Repository
public class WishlistRepository {

    private final JdbcTemplate jdbcTemplate;

    public WishlistRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<WishlistItem> wishlistRowMapper = (rs, rowNum) -> {
        WishlistItem w = new WishlistItem();
        w.setId(rs.getInt("id"));
        w.setUserId(rs.getInt("user_id"));
        w.setProductId(rs.getInt("product_id"));
        w.setProductName(rs.getString("name"));
        w.setImageUrl(rs.getString("image_url"));
        w.setPrice(rs.getDouble("price"));
        return w;
    };

    public List<WishlistItem> findByUserId(int userId) {
        String sql = "SELECT w.id, w.user_id, w.product_id, p.name, p.image_url, p.price " +
                     "FROM wishlist w JOIN products p ON w.product_id = p.id WHERE w.user_id = ?";
        return jdbcTemplate.query(sql, wishlistRowMapper, userId);
    }

    public void add(int userId, int productId) {
        jdbcTemplate.update(
            "INSERT IGNORE INTO wishlist (user_id, product_id) VALUES (?, ?)", userId, productId
        );
    }

    public void remove(int userId, int productId) {
        jdbcTemplate.update("DELETE FROM wishlist WHERE user_id = ? AND product_id = ?", userId, productId);
    }
}
