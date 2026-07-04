package com.novacart.controller;

import com.novacart.model.WishlistItem;
import com.novacart.service.WishlistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** REST endpoints for the wishlist. Base path: /api/wishlist */
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/{userId}")
    public List<WishlistItem> getWishlist(@PathVariable int userId) {
        return wishlistService.getWishlist(userId);
    }

    @PostMapping
    public Map<String, Object> add(@RequestBody Map<String, Integer> body) {
        wishlistService.addToWishlist(body.get("userId"), body.get("productId"));
        return Map.of("success", true, "message", "Added to wishlist");
    }

    @DeleteMapping("/{userId}/{productId}")
    public Map<String, Object> remove(@PathVariable int userId, @PathVariable int productId) {
        wishlistService.removeFromWishlist(userId, productId);
        return Map.of("success", true, "message", "Removed from wishlist");
    }
}
