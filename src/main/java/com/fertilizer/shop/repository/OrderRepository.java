package com.fertilizer.shop.repository;

import com.fertilizer.shop.model.Order;
import com.fertilizer.shop.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Order entity
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find orders by customer
     */
    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);
    
    /**
     * Find orders by customer with pagination
     */
    Page<Order> findByCustomer(User customer, Pageable pageable);
    
    /**
     * Find orders by status
     */
    List<Order> findByStatus(Order.Status status);
    
    /**
     * Find all orders ordered by creation date
     */
    List<Order> findAllByOrderByCreatedAtDesc();
    
    /**
     * Find all orders with pagination
     */
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Find orders by status and customer
     */
    List<Order> findByStatusAndCustomer(Order.Status status, User customer);
    
    /**
     * Find orders created after a specific date
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :date ORDER BY o.createdAt DESC")
    List<Order> findOrdersCreatedAfter(@Param("date") LocalDateTime date);
    
    /**
     * Count orders by status
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") Order.Status status);
    
    /**
     * Get total revenue (sum of all confirmed/paid orders)
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status IN :statuses")
    Double getTotalRevenue(@Param("statuses") List<Order.Status> statuses);
    
    /**
     * Get total revenue for a specific customer
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.customer = :customer AND o.status IN :statuses")
    Double getTotalRevenueByCustomer(@Param("customer") User customer, @Param("statuses") List<Order.Status> statuses);
    
    /**
     * Find recent orders (last 30 days)
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(@Param("startDate") LocalDateTime startDate);
}
