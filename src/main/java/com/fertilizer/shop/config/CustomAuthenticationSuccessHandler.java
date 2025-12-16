package com.fertilizer.shop.config;

import com.fertilizer.shop.model.User;
import com.fertilizer.shop.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom authentication success handler to redirect based on user role
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {

        try {
            // Get user from database to check role
            String email = authentication.getName();
            System.out.println("Login success for user: " + email);

            User user = userService.findByEmail(email).orElse(null);
            System.out.println("User found: " + (user != null) + ", Role: " + (user != null ? user.getRole() : "null"));

            if (user != null && user.getRole() == User.Role.ADMIN) {
                System.out.println("Redirecting admin to /admin/dashboard");
                response.sendRedirect("/admin/dashboard");
            } else {
                System.out.println("Redirecting customer to /customer/dashboard");
                // Redirect customers to their dashboard
                response.sendRedirect("/customer/dashboard");
            }
        } catch (Exception e) {
            System.err.println("Error in authentication success handler: " + e.getMessage());
            e.printStackTrace();
            // Fallback to customer dashboard
            response.sendRedirect("/customer/dashboard");
        }
    }
}