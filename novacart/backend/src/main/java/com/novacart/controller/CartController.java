package com.novacart.controller;

import com.novacart.model.CartItem;
import com.novacart.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** REST endpoints for the shopping cart. Base path: /api/cart */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public List<CartItem> getCart(@PathVariable int userId) {
        return cartService.getCart(userId);
    }

    @PostMapping
    public Map<String, Object> addToCart(@RequestBody Map<String, Integer> body) {
        cartService.addToCart(body.get("userId"), body.get("productId"), body.getOrDefault("quantity", 1));
        return Map.of("success", true, "message", "Added to cart");
    }

    @PutMapping
    public Map<String, Object> updateQty(@RequestBody Map<String, Integer> body) {
        cartService.updateQuantity(body.get("userId"), body.get("productId"), body.get("quantity"));
        return Map.of("success", true);
    }

    @DeleteMapping("/{userId}/{productId}")
    public Map<String, Object> remove(@PathVariable int userId, @PathVariable int productId) {
        cartService.removeFromCart(userId, productId);
        return Map.of("success", true, "message", "Removed from cart");
    }

    @DeleteMapping("/clear/{userId}")
    public Map<String, Object> clear(@PathVariable int userId) {
        cartService.clearCart(userId);
        return Map.of("success", true);
    }
}
