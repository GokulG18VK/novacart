package com.novacart.controller;

import com.novacart.model.Order;
import com.novacart.model.OrderItem;
import com.novacart.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * REST endpoints for placing orders and order history / admin order management.
 * Base path: /api/orders
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/user/{userId}")
    public List<Order> getUserOrders(@PathVariable int userId) {
        return orderService.getOrdersByUser(userId);
    }

    /**
     * Places a new order. Expects a JSON body shaped like:
     * {
     *   "userId": 1,
     *   "total": 9999,
     *   "paymentMethod": "card",
     *   "items": [ { "id": 2, "name": "...", "price": 7999, "qty": 1 }, ... ],
     *   "address": { "fullName": "...", "phone": "...", "address": "...", "city": "...", "state": "...", "pincode": "..." }
     * }
     */
    @SuppressWarnings("unchecked")
    @PostMapping
    public Map<String, Object> placeOrder(@RequestBody Map<String, Object> body) {
        Order order = new Order();
        Object userIdObj = body.get("userId");
        order.setUserId(userIdObj != null ? ((Number) userIdObj).intValue() : null);
        order.setTotalAmount(new BigDecimal(body.get("total").toString()));
        order.setPaymentMethod((String) body.getOrDefault("paymentMethod", "card"));

        Map<String, Object> address = (Map<String, Object>) body.get("address");
        if (address != null) {
            order.setFullName((String) address.get("fullName"));
            order.setPhone((String) address.get("phone"));
            order.setAddress((String) address.get("address"));
            order.setCity((String) address.get("city"));
            order.setState((String) address.get("state"));
            order.setPincode((String) address.get("pincode"));
        }

        List<Map<String, Object>> rawItems = (List<Map<String, Object>>) body.get("items");
        List<OrderItem> items = new ArrayList<>();
        if (rawItems != null) {
            for (Map<String, Object> raw : rawItems) {
                OrderItem item = new OrderItem();
                Object pid = raw.get("productId");
                 if (pid == null) pid = raw.get("id");
                item.setProductId(pid != null ? ((Number) pid).intValue() : null);
                item.setProductName((String) raw.get("name"));
                item.setPrice(new BigDecimal(raw.get("price").toString()));
                Object qty = raw.getOrDefault("qty", 1);
                item.setQuantity(((Number) qty).intValue());
                items.add(item);
            }
        }

        return orderService.placeOrder(order, items);
    }

    @PutMapping("/{orderId}/status")
    public Map<String, Object> updateStatus(@PathVariable int orderId, @RequestBody Map<String, String> body) {
        orderService.updateStatus(orderId, body.get("status"));
        return Map.of("success", true, "message", "Order status updated");
    }
}
