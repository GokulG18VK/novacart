package com.novacart.repository;

import com.novacart.model.Order;
import com.novacart.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/** Direct JDBC data-access layer for the `orders` and `order_items` tables. */
@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Order> orderRowMapper = (rs, rowNum) -> {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setOrderCode(rs.getString("order_code"));
        o.setUserId((Integer) rs.getObject("user_id"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setStatus(rs.getString("status"));
        o.setFullName(rs.getString("full_name"));
        o.setPhone(rs.getString("phone"));
        o.setAddress(rs.getString("address"));
        o.setCity(rs.getString("city"));
        o.setState(rs.getString("state"));
        o.setPincode(rs.getString("pincode"));
        java.sql.Timestamp created = rs.getTimestamp("created_at");
if (created != null) {
    o.setCreatedAt(created.toLocalDateTime());
}
        return o;
    };

    private final RowMapper<OrderItem> itemRowMapper = (rs, rowNum) -> {
        OrderItem i = new OrderItem();
        i.setId(rs.getInt("id"));
        i.setOrderId(rs.getInt("order_id"));
        i.setProductId((Integer) rs.getObject("product_id"));
        i.setProductName(rs.getString("product_name"));
        i.setPrice(rs.getBigDecimal("price"));
        i.setQuantity(rs.getInt("quantity"));
        return i;
    };

    public List<Order> findAll() {
        return jdbcTemplate.query("SELECT * FROM orders ORDER BY id DESC", orderRowMapper);
    }

    public List<Order> findByUserId(int userId) {
        return jdbcTemplate.query("SELECT * FROM orders WHERE user_id = ? ORDER BY id DESC", orderRowMapper, userId);
    }

    public Optional<Order> findById(int id) {
        List<Order> result = jdbcTemplate.query("SELECT * FROM orders WHERE id = ?", orderRowMapper, id);
        return result.stream().findFirst();
    }

    public List<OrderItem> findItemsByOrderId(int orderId) {
        return jdbcTemplate.query("SELECT * FROM order_items WHERE order_id = ?", itemRowMapper, orderId);
    }

    /** Inserts the order header row and returns the generated order id. */
    public int saveOrder(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO orders (order_code, user_id, total_amount, payment_method, status, full_name, phone, address, city, state, pincode) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, order.getOrderCode());
            ps.setObject(2, order.getUserId());
            ps.setBigDecimal(3, order.getTotalAmount());
            ps.setString(4, order.getPaymentMethod());
            ps.setString(5, order.getStatus());
            ps.setString(6, order.getFullName());
            ps.setString(7, order.getPhone());
            ps.setString(8, order.getAddress());
            ps.setString(9, order.getCity());
            ps.setString(10, order.getState());
            ps.setString(11, order.getPincode());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /** Inserts a single order item row (called once per cart item during checkout). */
    public void saveOrderItem(int orderId, OrderItem item) {
        jdbcTemplate.update(
            "INSERT INTO order_items (order_id, product_id, product_name, price, quantity) VALUES (?, ?, ?, ?, ?)",
            orderId, item.getProductId(), item.getProductName(), item.getPrice(), item.getQuantity()
        );
    }

    public void updateStatus(int orderId, String status) {
    jdbcTemplate.update("UPDATE orders SET status = ? WHERE id = ?", status, orderId);
}

public void increaseReviewCountForOrder(int orderId) {
    jdbcTemplate.update(
        "UPDATE products p " +
        "JOIN order_items oi ON p.id = oi.product_id " +
        "SET p.reviews_count = p.reviews_count + oi.quantity " +
        "WHERE oi.order_id = ?",
        orderId
    );
}
}