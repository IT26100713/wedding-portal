package com.WD08.WeddingMangementSystem.admin;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminSchema {
    private final JdbcTemplate jdbc;

    public AdminSchema(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void initialize() {
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS vendors (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY, business_name VARCHAR(100) NOT NULL,
                  owner_name VARCHAR(100) NOT NULL, email VARCHAR(255) NOT NULL UNIQUE,
                  password_hash VARCHAR(255) NOT NULL, phone VARCHAR(20), city VARCHAR(60),
                  category VARCHAR(30) NOT NULL, description VARCHAR(500), base_rate DOUBLE NOT NULL DEFAULT 0
                ) ENGINE=InnoDB
                """);
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS vendor_packages (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY, vendor_id BIGINT NOT NULL,
                  name VARCHAR(100) NOT NULL, description VARCHAR(300), price DOUBLE NOT NULL,
                  FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
                ) ENGINE=InnoDB
                """);
        addColumn("approval_status", "VARCHAR(20) NOT NULL DEFAULT 'PENDING'");
        addColumn("approval_reason", "VARCHAR(500) NOT NULL DEFAULT ''");
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS admin_activity (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY, occurred_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
                  actor VARCHAR(255) NOT NULL, action VARCHAR(50) NOT NULL,
                  target VARCHAR(255) NOT NULL, details VARCHAR(500) NOT NULL
                ) ENGINE=InnoDB
                """);
    }

    private void addColumn(String name, String definition) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='vendors' AND column_name=?", Integer.class, name);
        if (count == 0) jdbc.execute("ALTER TABLE vendors ADD COLUMN " + name + " " + definition);
    }
}
