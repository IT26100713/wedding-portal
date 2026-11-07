package com.WD08.WeddingMangementSystem.vendor.repository;

import com.WD08.WeddingMangementSystem.vendor.model.VendorPackage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public class PackageRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<VendorPackage> mapper = (rs, rowNum) -> {
        VendorPackage p = new VendorPackage();
        p.setId(rs.getLong("id"));
        p.setVendorId(rs.getLong("vendor_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));
        return p;
    };

    public PackageRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // CREATE
    public int save(VendorPackage p) {
        return jdbc.update(
                "INSERT INTO vendor_packages (vendor_id, name, description, price) VALUES (?, ?, ?, ?)",
                p.getVendorId(), p.getName(), p.getDescription(), p.getPrice());
    }

    // READ
    public List<VendorPackage> findByVendorId(Long vendorId) {
        return jdbc.query(
                "SELECT * FROM vendor_packages WHERE vendor_id = ? ORDER BY id DESC", mapper, vendorId);
    }

    public Optional<VendorPackage> findByIdAndVendorId(Long id, Long vendorId) {
        List<VendorPackage> list = jdbc.query(
                "SELECT * FROM vendor_packages WHERE id = ? AND vendor_id = ?", mapper, id, vendorId);
        return list.stream().findFirst();
    }

    // UPDATE
    public int update(VendorPackage p) {
        return jdbc.update(
                "UPDATE vendor_packages SET name = ?, description = ?, price = ? "
                        + "WHERE id = ? AND vendor_id = ?",
                p.getName(), p.getDescription(), p.getPrice(), p.getId(), p.getVendorId());
    }

    // DELETE
    public int delete(Long id, Long vendorId) {
        return jdbc.update(
                "DELETE FROM vendor_packages WHERE id = ? AND vendor_id = ?", id, vendorId);
    }

}
