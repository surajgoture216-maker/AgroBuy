package com.fertilizer.shop.controller;

import com.fertilizer.shop.model.Order;
import com.fertilizer.shop.model.OrderItem;
import com.fertilizer.shop.model.Product;
import com.fertilizer.shop.model.User;
import com.fertilizer.shop.service.CartService;
import com.fertilizer.shop.service.OrderService;
import com.fertilizer.shop.service.ProductService;
import com.fertilizer.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for cart operations
 */
@Controller
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Show cart page
     */
    @GetMapping
    public String showCart(Model model) {
        List<OrderItem> cartItems = cartService.getCartItems();
        CartService.CartSummary cartSummary = cartService.getCartSummary();
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartSummary", cartSummary);
        model.addAttribute("isEmpty", cartService.isCartEmpty());
        
        return "customer/cart";
    }
    
    /**
     * Add product to cart
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addToCart(@RequestParam Long productId,
                                       @RequestParam(defaultValue = "1") int quantity) {

        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Product> productOpt = productService.findById(productId);
            if (productOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Product not found");
                return response;
            }

            Product product = productOpt.get();
            if (!product.getIsActive() || product.getStock() < quantity) {
                response.put("success", false);
                response.put("message", "Product is out of stock or unavailable");
                return response;
            }

            cartService.addToCart(product, quantity);

            response.put("success", true);
            response.put("message", "Product added to cart successfully!");
            response.put("cartCount", cartService.getCartSummary().getTotalItems());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error adding product to cart: " + e.getMessage());
        }

        return response;
    }
    
    /**
     * Update cart item quantity
     */
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateCart(@RequestParam Long productId,
                                         @RequestParam int quantity,
                                         HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (quantity > 0) {
                cartService.updateQuantity(productId, quantity);
            } else {
                cartService.removeFromCart(productId);
            }

            response.put("success", true);
            response.put("message", "Cart updated successfully");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update cart: " + e.getMessage());
        }

        return response;
    }
    
    /**
     * Remove item from cart
     */
    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId) {
        cartService.removeFromCart(productId);
        return "redirect:/cart";
    }
    
    /**
     * Clear entire cart
     */
    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }
    
    /**
     * Show checkout page
     */
    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        if (cartService.isCartEmpty()) {
            return "redirect:/cart";
        }
        
        // Get current user for default shipping address
        User currentUser = getCurrentUser();
        
        List<OrderItem> cartItems = cartService.getCartItems();
        CartService.CartSummary cartSummary = cartService.getCartSummary();
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartSummary", cartSummary);
        model.addAttribute("user", currentUser);
        model.addAttribute("shippingAddress", currentUser.getAddress());
        
        return "customer/checkout";
    }
    
    /**
     * Process checkout
     */
    @PostMapping("/checkout")
    public String processCheckout(@RequestParam String shippingAddress,
                                 @RequestParam String phoneNumber,
                                 RedirectAttributes redirectAttributes) {
        
        if (cartService.isCartEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Cannot checkout with empty cart");
            return "redirect:/cart";
        }
        
        try {
            User currentUser = getCurrentUser();
            
            // Validate stock for all items
            List<OrderItem> cartItems = cartService.getCartItems();
            for (OrderItem item : cartItems) {
                Product product = item.getProduct();
                if (!product.getIsActive() || product.getStock() < item.getQuantity()) {
                    redirectAttributes.addFlashAttribute("errorMessage", 
                        "Product '" + product.getName() + "' is out of stock");
                    return "redirect:/cart";
                }
            }
            
            // Create order
            Order order = orderService.createOrder(currentUser, cartItems, shippingAddress);
            
            // Reduce stock for all products
            for (OrderItem item : cartItems) {
                productService.reduceStock(item.getProduct().getId(), item.getQuantity());
            }
            
            // Clear cart
            cartService.clearCart();
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Order placed successfully! Order ID: " + order.getId());
            
            return "redirect:/customer/orders";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error placing order: " + e.getMessage());
            return "redirect:/cart/checkout";
        }
    }
    
    /**
     * Helper method to get current authenticated user
     */
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
