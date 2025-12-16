package com.fertilizer.shop.service;

import com.fertilizer.shop.model.User;
import com.fertilizer.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for User operations
 */
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new customer
     */
    public User registerCustomer(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.CUSTOMER);
        return userRepository.save(user);
    }
    
    /**
     * Save user (for updates)
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Find all customers
     */
    public List<User> findAllCustomers() {
        return userRepository.findByRole(User.Role.CUSTOMER);
    }
    
    /**
     * Find all customers with pagination
     */
    public org.springframework.data.domain.Page<User> findAllCustomers(org.springframework.data.domain.Pageable pageable) {
        return userRepository.findByRole(User.Role.CUSTOMER, pageable);
    }
    
    /**
     * Update user profile (excluding email and password)
     */
    public User updateProfile(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        existingUser.setName(updatedUser.getName());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Change user password
     */
    public User changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
    
    /**
     * Delete user
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
    
    /**
     * Count total users
     */
    public long countTotalUsers() {
        return userRepository.count();
    }
    
    /**
     * Count customers
     */
    public long countCustomers() {
        return userRepository.countByRole(User.Role.CUSTOMER);
    }

    /**
     * Update user role
     */
    public User updateUserRole(Long userId, User.Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(newRole);
        return userRepository.save(user);
    }

    /**
     * Activate or deactivate user
     */
    public User updateUserActiveStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Note: We don't actually deactivate users in this implementation
        // Instead, we could add an isActive field to User entity if needed
        return userRepository.save(user);
    }
}
