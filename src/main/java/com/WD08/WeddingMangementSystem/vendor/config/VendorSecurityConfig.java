package com.WD08.WeddingMangementSystem.vendor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity

public class VendorSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain vendorFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/vendor/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

}
