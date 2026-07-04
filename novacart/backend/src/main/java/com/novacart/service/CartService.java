package com.novacart.service;

import com.novacart.model.CartItem;
import com.novacart.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** Business logic for the shopping cart. */
@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public List<CartItem> getCart(int userId) {
        return cartRepository.findByUserId(userId);
    }

    public void addToCart(int userId, int productId, int quantity) {
        cartRepository.addOrUpdate(userId, productId, quantity);
    }

    public void updateQuantity(int userId, int productId, int quantity) {
        if (quantity <= 0) {
            cartRepository.remove(userId, productId);
        } else {
            cartRepository.updateQuantity(userId, productId, quantity);
        }
    }

    public void removeFromCart(int userId, int productId) {
        cartRepository.remove(userId, productId);
    }

    public void clearCart(int userId) {
        cartRepository.clearCart(userId);
    }
}
