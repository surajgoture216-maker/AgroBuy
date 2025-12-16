package com.fertilizer.shop.controller;

import com.fertilizer.shop.model.Order;
import com.fertilizer.shop.model.Product;
import com.fertilizer.shop.model.User;
import com.fertilizer.shop.model.dto.ProductDto;
import com.fertilizer.shop.service.OrderService;
import com.fertilizer.shop.service.ProductService;
import com.fertilizer.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

/**
 * Controller for admin operations
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * Show admin dashboard
     */
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Get statistics
        long totalProducts = productService.countActiveProducts();
        long totalCustomers = userService.countCustomers();
        long totalOrders = orderService.findAllOrders().size();
        
        OrderService.OrderStatistics orderStats = orderService.getOrderStatistics();
        
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("orderStats", orderStats);
        
        return "admin/admin-dashboard";
    }
    
    /**
     * Show products management page
     */
    @GetMapping("/products")
    public String showProducts(@RequestParam(defaultValue = "0") int page,
                              Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Product> products = productService.findAllActiveProducts(pageable);
        
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        
        return "admin/admin-products";
    }
    
    /**
     * Show add product form
     */
    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("isEdit", false);
        return "admin/admin-product-form";
    }
    
    /**
     * Show edit product form
     */
    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setPrice(product.getPrice());
        productDto.setStock(product.getStock());
        productDto.setCategory(product.getCategory());
        productDto.setBrand(product.getBrand());
        productDto.setWeight(product.getWeight());
        
        model.addAttribute("productDto", productDto);
        model.addAttribute("isEdit", true);
        return "admin/admin-product-form";
    }
    
    /**
     * Save product (create or update)
     */
    @PostMapping("/products/save")
    public String saveProduct(@Valid @ModelAttribute("productDto") ProductDto productDto,
                             BindingResult bindingResult,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/admin-product-form";
        }

        try {
            Product product;

            if (productDto.getId() != null) {
                // Update existing product
                product = productService.findById(productDto.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            } else {
                // Create new product
                product = new Product();
                product.setCreatedAt(LocalDateTime.now());
            }

            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setPrice(productDto.getPrice());
            product.setStock(productDto.getStock());
            product.setCategory(productDto.getCategory());
            product.setBrand(productDto.getBrand());
            product.setWeight(productDto.getWeight());

            // Handle image upload
            if (!imageFile.isEmpty()) {
                String uploadDir = "uploads/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFilename = imageFile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(imageFile.getInputStream(), filePath);

                product.setImageUrl("/uploads/" + uniqueFilename);
            } else if (productDto.getId() == null) {
                // New product without image
                product.setImageUrl("https://via.placeholder.com/300x200/28a745/ffffff?text=No+Image");
            }
            // For updates, keep existing image if no new file uploaded

            productService.saveProduct(product);

            redirectAttributes.addFlashAttribute("successMessage",
                productDto.getId() == null ? "Product added successfully!" : "Product updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Error saving product: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }
    
    /**
     * Delete product
     */
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting product: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }
    
    /**
     * Show customers management page
     */
    @GetMapping("/customers")
    public String showCustomers(@RequestParam(defaultValue = "0") int page,
                               Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<User> customers = userService.findAllCustomers(pageable);
        
        model.addAttribute("customers", customers);
        model.addAttribute("currentPage", page);
        
        return "admin/admin-customers";
    }
    
    /**
     * Show customer orders
     */
    @GetMapping("/customers/{customerId}/orders")
    public String showCustomerOrders(@PathVariable Long customerId,
                                    Model model) {
        User customer = userService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        List<Order> orders = orderService.findByCustomer(customer);
        
        model.addAttribute("customer", customer);
        model.addAttribute("orders", orders);
        
        return "admin/admin-customer-orders";
    }
    
    /**
     * Show orders management page
     */
    @GetMapping("/orders")
    public String showOrders(@RequestParam(defaultValue = "0") int page,
                            Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Order> orders = orderService.findAllOrders(pageable);
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        
        return "admin/admin-orders";
    }
    
    /**
     * Show order details
     */
    @GetMapping("/orders/{orderId}")
    public String showOrderDetails(@PathVariable Long orderId, Model model) {
        Order order = orderService.findById(orderId);
        
        model.addAttribute("order", order);
        
        return "admin/admin-order-detail";
    }
    
    /**
     * Update order status
     */
    @PostMapping("/orders/{orderId}/status")
    public String updateOrderStatus(@PathVariable Long orderId,
                                   @RequestParam Order.Status status,
                                   RedirectAttributes redirectAttributes) {

        try {
            orderService.updateOrderStatus(orderId, status);
            redirectAttributes.addFlashAttribute("successMessage",
                "Order status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Error updating order status: " + e.getMessage());
        }

        return "redirect:/admin/orders";
    }

    /**
     * Promote user to admin
     */
    @PostMapping("/customers/{userId}/promote")
    public String promoteUserToAdmin(@PathVariable Long userId,
                                    RedirectAttributes redirectAttributes) {

        try {
            userService.updateUserRole(userId, User.Role.ADMIN);
            redirectAttributes.addFlashAttribute("successMessage",
                "User promoted to admin successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Error promoting user: " + e.getMessage());
        }

        return "redirect:/admin/customers";
    }
}
