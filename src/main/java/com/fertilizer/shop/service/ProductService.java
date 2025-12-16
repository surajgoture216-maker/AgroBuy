package com.fertilizer.shop.service;

import com.fertilizer.shop.model.Product;
import com.fertilizer.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Product operations
 */
@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * Find all active products
     */
    public List<Product> findAllActiveProducts() {
        return productRepository.findActiveProducts();
    }
    
    /**
     * Find all active products with pagination
     */
    public Page<Product> findAllActiveProducts(Pageable pageable) {
        return productRepository.findActiveProducts(pageable);
    }
    
    /**
     * Find product by ID
     */
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
    
    /**
     * Find active product by ID
     */
    public Optional<Product> findActiveProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.filter(Product::getIsActive);
    }
    
    /**
     * Save product (create or update)
     */
    public Product saveProduct(Product product) {
        if (product.getId() == null) {
            // Creating new product
            product.setIsActive(true);
        }
        return productRepository.save(product);
    }
    
    /**
     * Find products by category
     */
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    /**
     * Find products by name (search)
     */
    public List<Product> findByNameContaining(String name) {
        return productRepository.findByNameContaining(name);
    }
    
    /**
     * Find products by brand
     */
    public List<Product> findByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }
    
    /**
     * Find products in stock
     */
    public List<Product> findProductsInStock() {
        return productRepository.findProductsInStock();
    }
    
    /**
     * Delete product (soft delete)
     */
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setIsActive(false);
        productRepository.save(product);
    }
    
    /**
     * Update product stock
     */
    public Product updateStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setStock(quantity);
        return productRepository.save(product);
    }
    
    /**
     * Reduce product stock
     */
    public Product reduceStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }
        
        product.reduceStock(quantity);
        return productRepository.save(product);
    }
    
    /**
     * Count active products
     */
    public long countActiveProducts() {
        return productRepository.countActiveProducts();
    }
    
    /**
     * Check if product is in stock
     */
    public boolean isProductInStock(Long productId, int quantity) {
        return productRepository.isProductInStock(productId, quantity);
    }
    
    /**
     * Find products by multiple categories
     */
    public List<Product> findByCategories(List<String> categories) {
        return productRepository.findByCategories(categories);
    }
    
    /**
     * Get all product categories
     */
    public List<String> getAllCategories() {
        return productRepository.findAll().stream()
                .filter(Product::getIsActive)
                .map(Product::getCategory)
                .distinct()
                .toList();
    }
    
    /**
     * Get all brands
     */
    public List<String> getAllBrands() {
        return productRepository.findAll().stream()
                .filter(Product::getIsActive)
                .map(Product::getBrand)
                .filter(brand -> brand != null && !brand.isEmpty())
                .distinct()
                .toList();
    }
}
