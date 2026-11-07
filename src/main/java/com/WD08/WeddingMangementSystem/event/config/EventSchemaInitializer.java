package com.WD08.WeddingMangementSystem.event.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class EventSchemaInitializer {

    private final JdbcTemplate jdbcTemplate;

    public EventSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void createEventsTableIfMissing() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS events (
                    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    event_id VARCHAR(20) NOT NULL UNIQUE,
                    event_name VARCHAR(255) NOT NULL,
                    event_date DATE NOT NULL,
                    start_time TIME NOT NULL,
                    venue VARCHAR(255) NOT NULL,
                    guest_count INT NOT NULL,
                    estimated_budget DECIMAL(12, 2) NOT NULL,
                    status VARCHAR(255) NOT NULL,
                    event_type VARCHAR(255) NOT NULL,
                    description TEXT NOT NULL,
                    created_at DATETIME(6) NOT NULL,
                    updated_at DATETIME(6) NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
    }
}
