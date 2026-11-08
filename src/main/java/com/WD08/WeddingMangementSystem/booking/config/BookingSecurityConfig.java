package com.WD08.WeddingMangementSystem.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Keep booking pages open while other routes use the application's security rules.
@Configuration
public class BookingSecurityConfig {

    @Bean
    @Order(3)
    public SecurityFilterChain bookingFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/bookings", "/bookings/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
