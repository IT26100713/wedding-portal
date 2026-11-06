package com.WD08.WeddingMangementSystem.review.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Review & Feedback module: lets the review REST API (/api/**) and the review pages (/review/**) open
 * without the default Spring Security login page.
 *
 * It only matches URLs that start with /api/ or /review/, so every other page of the
 * group project keeps using the group's own security configuration.
 */
@Configuration
public class ReviewApiSecurityConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain reviewApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**", "/com/WD08/WeddingMangementSystem/review/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
