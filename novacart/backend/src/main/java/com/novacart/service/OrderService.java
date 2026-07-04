package com.novacart.service;

import com.novacart.model.Order;
import com.novacart.model.OrderItem;
import com.novacart.repository.CartRepository;
import com.novacart.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/** Business logic for placing and managing orders. */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        orders.forEach(o -> o.setItems(orderRepository.findItemsByOrderId(o.getId())));
        return orders;
    }

    public List<Order> getOrdersByUser(int userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        orders.forEach(o -> o.setItems(orderRepository.findItemsByOrderId(o.getId())));
        return orders;
    }

    /**
     * Places a new order: inserts the order header, then each order_item row,
     * and finally clears the user's cart. Wrapped in @Transactional so that
     * if any insert fails, the whole operation rolls back.
     */
    @Transactional
    public Map<String, Object> placeOrder(Order order, List<OrderItem> items) {
        // Generate a human-friendly order code, e.g. NC + timestamp
        String orderCode = "NC" + System.currentTimeMillis();
        order.setOrderCode(orderCode);
        order.setStatus("Pending");

        int orderId = orderRepository.saveOrder(order);
        for (OrderItem item : items) {
            item.setOrderId(orderId);
            orderRepository.saveOrderItem(orderId, item);
        }

        if (order.getUserId() != null) {
            cartRepository.clearCart(order.getUserId());
        }

        return Map.of("success", true, "orderId", orderCode, "internalId", orderId);
    }

    public void updateStatus(int orderId, String status) {
    Order oldOrder = orderRepository.findById(orderId).orElse(null);

    orderRepository.updateStatus(orderId, status);

    if (oldOrder != null
            && !"Delivered".equalsIgnoreCase(oldOrder.getStatus())
            && "Delivered".equalsIgnoreCase(status)) {
        orderRepository.increaseReviewCountForOrder(orderId);
    }
}
}
