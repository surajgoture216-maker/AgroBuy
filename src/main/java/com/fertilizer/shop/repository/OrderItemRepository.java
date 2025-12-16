package com.fertilizer.shop.repository;

import com.fertilizer.shop.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for OrderItem entity
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Basic CRUD operations are inherited from JpaRepository
    // No additional custom methods needed as OrderItem is mainly accessed through Order
}
