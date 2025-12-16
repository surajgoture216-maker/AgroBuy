package com.fertilizer.shop.controller;

import com.fertilizer.shop.model.Product;
import com.fertilizer.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private ProductService productService;

    @ModelAttribute("categories")
    public List<String> getCategories() {
        return productService.getAllCategories();
    }
    
    @GetMapping("/")
    public String home(Model model) {
        List<Product> featuredProducts = productService.findAllActiveProducts().stream()
                .limit(8)
                .toList();
        model.addAttribute("featuredProducts", featuredProducts);
        return "index";
    }
    
    @GetMapping("/products")
    public String products(@RequestParam(required = false) String category,
                          @RequestParam(required = false) String search,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        
        PageRequest pageRequest = PageRequest.of(page, 12);
        Page<Product> products;
        
        if (category != null && !category.isEmpty()) {
            List<Product> productList = productService.findByCategory(category);
            products = new org.springframework.data.domain.PageImpl<>(productList, pageRequest, productList.size());
        } else if (search != null && !search.isEmpty()) {
            List<Product> productList = productService.findByNameContaining(search);
            products = new org.springframework.data.domain.PageImpl<>(productList, pageRequest, productList.size());
        } else {
            products = productService.findAllActiveProducts(pageRequest);
        }
        
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("category", category);
        model.addAttribute("search", search);
        
        return "customer/product-list";
    }
    
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        return "customer/product-detail";
    }
}
