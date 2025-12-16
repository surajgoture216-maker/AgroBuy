package com.fertilizer.shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security configuration for the application
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;


    
    /**
     * Configure SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .userDetailsService(customUserDetailsService)
            .authorizeHttpRequests(authz -> authz
                // Static resources - must be first
                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**", "/webjars/**", "/uploads/**").permitAll()
                .requestMatchers("/favicon.ico").permitAll()

                // Public pages
                .requestMatchers("/", "/products", "/products/**", "/auth/**", "/error", "/debug/**").permitAll()

                // Admin pages - only ADMIN role
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Customer pages - authenticated customers only
                .requestMatchers("/customer/**", "/cart/**", "/orders/**", "/profile/**").hasRole("CUSTOMER")

                // Any other request needs authentication
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .successHandler(customAuthenticationSuccessHandler)
                .failureUrl("/auth/login-error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/auth/access-denied")
            );
            
        return http.build();
    }
    
}
