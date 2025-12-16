package com.fertilizer.shop.repository;

import com.fertilizer.shop.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Find active products
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true")
    List<Product> findActiveProducts();
    
    /**
     * Find active products with pagination
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true")
    Page<Product> findActiveProducts(Pageable pageable);
    
    /**
     * Find products by category
     */
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.isActive = true")
    List<Product> findByCategory(@Param("category") String category);
    
    /**
     * Find products by name (case insensitive)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.isActive = true")
    List<Product> findByNameContaining(@Param("name") String name);
    
    /**
     * Find products by brand
     */
    @Query("SELECT p FROM Product p WHERE p.brand = :brand AND p.isActive = true")
    List<Product> findByBrand(@Param("brand") String brand);
    
    /**
     * Find products in stock
     */
    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.isActive = true")
    List<Product> findProductsInStock();
    
    /**
     * Count active products
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = true")
    long countActiveProducts();
    
    /**
     * Find products by multiple categories
     */
    @Query("SELECT p FROM Product p WHERE p.category IN :categories AND p.isActive = true")
    List<Product> findByCategories(@Param("categories") List<String> categories);
    
    /**
     * Check if product is in stock
     */
    @Query("SELECT CASE WHEN p.stock >= :quantity THEN true ELSE false END FROM Product p WHERE p.id = :id")
    boolean isProductInStock(@Param("id") Long id, @Param("quantity") int quantity);
}
