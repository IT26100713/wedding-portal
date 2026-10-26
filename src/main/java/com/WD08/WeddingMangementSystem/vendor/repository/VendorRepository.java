package com.WD08.WeddingMangementSystem.vendor.repository;

import com.WD08.WeddingMangementSystem.vendor.model.Vendor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class VendorRepository {

    private final JdbcTemplate jdbc;
    private final VendorRowMapper mapper = new VendorRowMapper();

    // Spring injects the JdbcTemplate (built from application.properties)
    public VendorRepository(JdbcTemplate jdbc) {

        this.jdbc = jdbc;
    }

    // CREATE
    public Long save(Vendor v) {
        String sql = "INSERT INTO vendors (business_name, owner_name, email, password_hash, "
                + "phone, city, category, description, base_rate) VALUES (?,?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, v.getBusinessName());
            ps.setString(2, v.getOwnerName());
            ps.setString(3, v.getEmail());
            ps.setString(4, v.getPasswordHash());
            ps.setString(5, v.getPhone());
            ps.setString(6, v.getCity());
            ps.setString(7, v.getCategory());
            ps.setString(8, v.getDescription());
            ps.setDouble(9, v.getBaseRate());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? null : key.longValue();
    }

    // READ
    public Optional<Vendor> findById(Long id) {
        List<Vendor> list = jdbc.query("SELECT * FROM vendors WHERE id = ?", mapper, id);
        return list.stream().findFirst();
    }

    public Optional<Vendor> findByEmail(String email) {
        List<Vendor> list = jdbc.query("SELECT * FROM vendors WHERE email = ?", mapper, email);
        return list.stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM vendors WHERE email = ?", Integer.class, email);
        return count != null && count > 0;
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM vendors WHERE email = ? AND id <> ?", Integer.class, email, id);
        return count != null && count > 0;
    }

    // UPDATE (category and password are not changed here)
    public int update(Vendor v) {
        return jdbc.update(
                "UPDATE vendors SET business_name = ?, owner_name = ?, email = ?, phone = ?, "
                        + "city = ?, description = ?, base_rate = ? WHERE id = ?",
                v.getBusinessName(), v.getOwnerName(), v.getEmail(), v.getPhone(),
                v.getCity(), v.getDescription(), v.getBaseRate(), v.getId());
    }

    // DELETE
    public int delete(Long id) {

        return jdbc.update("DELETE FROM vendors WHERE id = ?", id);
    }
}
