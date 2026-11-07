package com.WD08.WeddingMangementSystem.event.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class EventSecurityConfig {

    @Bean
    @Order(2)
    public SecurityFilterChain eventFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/events/**", "/event/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
