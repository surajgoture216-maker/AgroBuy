package com.fertilizer.shop.service;

import com.fertilizer.shop.model.Order;
import com.fertilizer.shop.model.OrderItem;
import com.fertilizer.shop.model.Product;
import com.fertilizer.shop.model.User;
import com.fertilizer.shop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for Order operations
 */
@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    /**
     * Create a new order
     */
    public Order createOrder(User customer, List<OrderItem> orderItems, String shippingAddress) {
        // Calculate total amount
        Double totalAmount = orderItems.stream()
                .mapToDouble(item -> item.getSubtotal())
                .sum();
        
        Order order = new Order(customer, totalAmount, shippingAddress);
        
        // Add order items to order
        orderItems.forEach(item -> {
            order.addOrderItem(item);
            item.setPriceAtPurchase(item.getProduct().getPrice());
        });
        
        return orderRepository.save(order);
    }
    
    /**
     * Find order by ID
     */
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    /**
     * Find orders by customer
     */
    public List<Order> findByCustomer(User customer) {
        return orderRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }
    
    /**
     * Find orders by customer with pagination
     */
    public Page<Order> findByCustomer(User customer, Pageable pageable) {
        return orderRepository.findByCustomer(customer, pageable);
    }
    
    /**
     * Find all orders
     */
    public List<Order> findAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * Find all orders with pagination
     */
    public Page<Order> findAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    /**
     * Update order status
     */
    public Order updateOrderStatus(Long orderId, Order.Status newStatus) {
        Order order = findById(orderId);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
    
    /**
     * Cancel order
     */
    public Order cancelOrder(Long orderId) {
        Order order = findById(orderId);
        if (!order.canBeCancelled()) {
            throw new RuntimeException("Order cannot be cancelled");
        }
        order.setStatus(Order.Status.CANCELLED);
        return orderRepository.save(order);
    }
    
    /**
     * Find orders by status
     */
    public List<Order> findByStatus(Order.Status status) {
        return orderRepository.findByStatus(status);
    }
    
    /**
     * Find orders by status and customer
     */
    public List<Order> findByStatusAndCustomer(Order.Status status, User customer) {
        return orderRepository.findByStatusAndCustomer(status, customer);
    }
    
    /**
     * Get total revenue
     */
    public Double getTotalRevenue() {
        List<Order.Status> paidStatuses = List.of(
                Order.Status.CONFIRMED, 
                Order.Status.SHIPPED, 
                Order.Status.DELIVERED
        );
        return orderRepository.getTotalRevenue(paidStatuses);
    }
    
    /**
     * Get total revenue by customer
     */
    public Double getTotalRevenueByCustomer(User customer) {
        List<Order.Status> paidStatuses = List.of(
                Order.Status.CONFIRMED, 
                Order.Status.SHIPPED, 
                Order.Status.DELIVERED
        );
        return orderRepository.getTotalRevenueByCustomer(customer, paidStatuses);
    }
    
    /**
     * Count orders by status
     */
    public long countOrdersByStatus(Order.Status status) {
        return orderRepository.countByStatus(status);
    }
    
    /**
     * Find recent orders (last 30 days)
     */
    public List<Order> findRecentOrders() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return orderRepository.findRecentOrders(thirtyDaysAgo);
    }
    
    /**
     * Delete order
     */
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Order not found");
        }
        orderRepository.deleteById(orderId);
    }
    
    /**
     * Get order statistics for dashboard
     */
    public OrderStatistics getOrderStatistics() {
        OrderStatistics stats = new OrderStatistics();
        stats.setTotalOrders(orderRepository.count());
        stats.setPendingOrders(orderRepository.countByStatus(Order.Status.PENDING));
        stats.setConfirmedOrders(orderRepository.countByStatus(Order.Status.CONFIRMED));
        stats.setShippedOrders(orderRepository.countByStatus(Order.Status.SHIPPED));
        stats.setDeliveredOrders(orderRepository.countByStatus(Order.Status.DELIVERED));
        stats.setCancelledOrders(orderRepository.countByStatus(Order.Status.CANCELLED));
        stats.setTotalRevenue(getTotalRevenue());
        return stats;
    }
    
    /**
     * Statistics class for dashboard
     */
    public static class OrderStatistics {
        private long totalOrders;
        private long pendingOrders;
        private long confirmedOrders;
        private long shippedOrders;
        private long deliveredOrders;
        private long cancelledOrders;
        private Double totalRevenue;
        
        // Getters and Setters
        public long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
        
        public long getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }
        
        public long getConfirmedOrders() { return confirmedOrders; }
        public void setConfirmedOrders(long confirmedOrders) { this.confirmedOrders = confirmedOrders; }
        
        public long getShippedOrders() { return shippedOrders; }
        public void setShippedOrders(long shippedOrders) { this.shippedOrders = shippedOrders; }
        
        public long getDeliveredOrders() { return deliveredOrders; }
        public void setDeliveredOrders(long deliveredOrders) { this.deliveredOrders = deliveredOrders; }
        
        public long getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(long cancelledOrders) { this.cancelledOrders = cancelledOrders; }
        
        public Double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    }
}
