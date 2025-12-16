package com.fertilizer.shop.controller;

import com.fertilizer.shop.model.Order;
import com.fertilizer.shop.model.User;
import com.fertilizer.shop.model.dto.UserDto;
import com.fertilizer.shop.service.OrderService;
import com.fertilizer.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller for customer operations
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    /**
     * Show customer dashboard
     */
    @GetMapping("/dashboard")
    public String customerDashboard(Model model) {
        User currentUser = getCurrentUser();

        // Get user's order statistics
        List<Order> userOrders = orderService.findByCustomer(currentUser);
        long totalOrders = userOrders.size();
        long pendingOrders = userOrders.stream().filter(o -> o.getStatus().name().equals("PENDING")).count();
        long completedOrders = userOrders.stream().filter(o -> o.getStatus().name().equals("DELIVERED")).count();

        model.addAttribute("user", currentUser);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("completedOrders", completedOrders);

        return "customer/dashboard";
    }

    /**
     * Show customer profile
     */
    @GetMapping("/profile")
    public String showProfile(Model model) {
        User currentUser = getCurrentUser();

        UserDto userDto = new UserDto();
        userDto.setName(currentUser.getName());
        userDto.setEmail(currentUser.getEmail());
        userDto.setPhone(currentUser.getPhone());
        userDto.setAddress(currentUser.getAddress());

        model.addAttribute("userDto", userDto);
        model.addAttribute("user", currentUser);
        return "customer/profile";
    }

    /**
     * Update customer profile
     */
    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("userDto") UserDto userDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "customer/profile";
        }

        try {
            User currentUser = getCurrentUser();

            // Update user information
            currentUser.setName(userDto.getName());
            currentUser.setPhone(userDto.getPhone());
            currentUser.setAddress(userDto.getAddress());

            userService.saveUser(currentUser);

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }

        return "redirect:/customer/profile";
    }

    /**
     * Show customer orders
     */
    @GetMapping("/orders")
    public String showOrders(@RequestParam(defaultValue = "0") int page, Model model) {
        User currentUser = getCurrentUser();

        Pageable pageable = PageRequest.of(page, 10);
        Page<Order> orders = orderService.findByCustomer(currentUser, pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);

        return "customer/orders";
    }

    /**
     * Show order details
     */
    @GetMapping("/orders/{orderId}")
    public String showOrderDetails(@PathVariable Long orderId, Model model) {
        User currentUser = getCurrentUser();
        Order order = orderService.findById(orderId);

        // Check if order belongs to current user
        if (!order.getCustomer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        model.addAttribute("order", order);
        return "customer/order-detail";
    }

    /**
     * Helper method to get current authenticated user
     */
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
