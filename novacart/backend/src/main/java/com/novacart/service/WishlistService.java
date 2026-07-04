package com.novacart.service;

import com.novacart.model.WishlistItem;
import com.novacart.repository.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** Business logic for the wishlist. */
@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public List<WishlistItem> getWishlist(int userId) {
        return wishlistRepository.findByUserId(userId);
    }

    public void addToWishlist(int userId, int productId) {
        wishlistRepository.add(userId, productId);
    }

    public void removeFromWishlist(int userId, int productId) {
        wishlistRepository.remove(userId, productId);
    }
}
