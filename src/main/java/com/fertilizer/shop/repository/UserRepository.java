package com.fertilizer.shop.repository;

import com.fertilizer.shop.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users by role
     */
    List<User> findByRole(User.Role role);
    
    /**
     * Find customers (excluding admins)
     */
    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findCustomers(@Param("role") User.Role role);
    
    /**
     * Find users by role with pagination
     */
    Page<User> findByRole(User.Role role, Pageable pageable);
    
    /**
     * Count total users
     */
    long count();
    
    /**
     * Count customers only
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") User.Role role);
}
