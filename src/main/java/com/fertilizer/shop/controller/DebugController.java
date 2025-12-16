package com.fertilizer.shop.controller;

import com.fertilizer.shop.model.User;
import com.fertilizer.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DebugController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/debug/users")
    public String listUsers() {
        List<User> users = userRepository.findAll();
        StringBuilder sb = new StringBuilder("Users in database:\n\n");
        for (User user : users) {
            sb.append("Email: ").append(user.getEmail())
              .append(", Role: ").append(user.getRole())
              .append(", Password hash: ").append(user.getPassword().substring(0, 30))
              .append("...\n");
        }
        return sb.toString();
    }
}
