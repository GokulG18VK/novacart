package com.novacart.repository;

import com.novacart.model.CartItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Direct JDBC data-access layer for the `cart` table (joined with products). */
@Repository
public class CartRepository {

    private final JdbcTemplate jdbcTemplate;

    public CartRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CartItem> cartRowMapper = (rs, rowNum) -> {
        CartItem c = new CartItem();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setProductId(rs.getInt("product_id"));
        c.setQuantity(rs.getInt("quantity"));
        c.setProductName(rs.getString("name"));
        c.setPrice(rs.getBigDecimal("price"));
        c.setImageUrl(rs.getString("image_url"));
        return c;
    };

    public List<CartItem> findByUserId(int userId) {
        String sql = "SELECT ct.id, ct.user_id, ct.product_id, ct.quantity, p.name, p.price, p.image_url " +
                     "FROM cart ct JOIN products p ON ct.product_id = p.id WHERE ct.user_id = ?";
        return jdbcTemplate.query(sql, cartRowMapper, userId);
    }

    /** Adds item to cart, or increments quantity if it already exists (upsert via ON DUPLICATE KEY). */
    public void addOrUpdate(int userId, int productId, int quantity) {
        String sql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
        jdbcTemplate.update(sql, userId, productId, quantity);
    }

    public void updateQuantity(int userId, int productId, int quantity) {
        jdbcTemplate.update("UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?", quantity, userId, productId);
    }

    public void remove(int userId, int productId) {
        jdbcTemplate.update("DELETE FROM cart WHERE user_id = ? AND product_id = ?", userId, productId);
    }

    public void clearCart(int userId) {
        jdbcTemplate.update("DELETE FROM cart WHERE user_id = ?", userId);
    }
}
