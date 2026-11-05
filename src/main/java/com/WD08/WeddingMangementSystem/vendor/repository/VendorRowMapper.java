package com.WD08.WeddingMangementSystem.vendor.repository;

import com.WD08.WeddingMangementSystem.vendor.model.Vendor;
import com.WD08.WeddingMangementSystem.vendor.model.VendorFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VendorRowMapper implements RowMapper<Vendor> {

    @Override
    public Vendor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Vendor vendor = VendorFactory.create(rs.getString("category"));
        vendor.setId(rs.getLong("id"));
        vendor.setBusinessName(rs.getString("business_name"));
        vendor.setOwnerName(rs.getString("owner_name"));
        vendor.setEmail(rs.getString("email"));
        vendor.setPasswordHash(rs.getString("password_hash"));
        vendor.setPhone(rs.getString("phone"));
        vendor.setCity(rs.getString("city"));
        vendor.setDescription(rs.getString("description"));
        vendor.setBaseRate(rs.getDouble("base_rate"));
        return vendor;
    }
}
