package com.fertilizer.shop.service;

import com.fertilizer.shop.model.OrderItem;
import com.fertilizer.shop.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class for managing shopping cart
 */
@Service
@SessionScope
public class CartService {
    
    private final List<OrderItem> cartItems = new ArrayList<>();
    
    /**
     * Add product to cart
     */
    public void addToCart(Product product, int quantity) {
        // Check if product already exists in cart
        Optional<OrderItem> existingItem = cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();
        
        if (existingItem.isPresent()) {
            // Update quantity
            OrderItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // Add new item
            OrderItem newItem = new OrderItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPriceAtPurchase(product.getPrice());
            cartItems.add(newItem);
        }
    }
    
    /**
     * Remove item from cart
     */
    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }
    
    /**
     * Update quantity of item in cart
     */
    public void updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(productId);
        } else {
            cartItems.stream()
                    .filter(item -> item.getProduct().getId().equals(productId))
                    .forEach(item -> item.setQuantity(quantity));
        }
    }
    
    /**
     * Get all cart items
     */
    public List<OrderItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }
    
    /**
     * Get cart total amount
     */
    public Double getCartTotal() {
        return cartItems.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }
    
    /**
     * Get cart total items count
     */
    public int getCartTotalItems() {
        return cartItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
    
    /**
     * Clear cart
     */
    public void clearCart() {
        cartItems.clear();
    }
    
    /**
     * Check if cart is empty
     */
    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }
    
    /**
     * Get item from cart by product ID
     */
    public Optional<OrderItem> getCartItemByProductId(Long productId) {
        return cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }
    
    /**
     * Check if product is in cart
     */
    public boolean isProductInCart(Long productId) {
        return getCartItemByProductId(productId).isPresent();
    }
    
    /**
     * Get cart summary for display
     */
    public CartSummary getCartSummary() {
        return new CartSummary(
                getCartTotalItems(),
                getCartTotal(),
                cartItems.size()
        );
    }
    
    /**
     * Cart summary class
     */
    public static class CartSummary {
        private final int totalItems;
        private final Double totalPrice;
        private final int totalProducts;
        
        public CartSummary(int totalItems, Double totalPrice, int totalProducts) {
            this.totalItems = totalItems;
            this.totalPrice = totalPrice;
            this.totalProducts = totalProducts;
        }
        
        public int getTotalItems() {
            return totalItems;
        }
        
        public Double getTotalPrice() {
            return totalPrice;
        }
        
        public int getTotalProducts() {
            return totalProducts;
        }
    }
}
