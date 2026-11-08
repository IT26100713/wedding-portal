package com.WD08.WeddingMangementSystem.admin;

import com.WD08.WeddingMangementSystem.DTO.UserUpdateDto;
import com.WD08.WeddingMangementSystem.Service.UserService;
import com.WD08.WeddingMangementSystem.vendor.dto.VendorForm;
import com.WD08.WeddingMangementSystem.vendor.service.VendorService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdminService {
    private final JdbcTemplate jdbc;
    private final UserService users;
    private final VendorService vendors;

    public AdminService(JdbcTemplate jdbc, UserService users, VendorService vendors) {
        this.jdbc = jdbc;
        this.users = users;
        this.vendors = vendors;
    }

    public List<Map<String, Object>> vendors(String search, String status) {
        return jdbc.queryForList("SELECT id,business_name,owner_name,email,phone,city,category,description,base_rate,approval_status,approval_reason FROM vendors WHERE (business_name LIKE ? OR email LIKE ?) AND (?='' OR approval_status=?) ORDER BY id DESC", "%" + search + "%", "%" + search + "%", status, status);
    }

    public List<Map<String, Object>> activity(int page) {
        return jdbc.queryForList("SELECT * FROM admin_activity ORDER BY id DESC LIMIT 50 OFFSET ?", Math.max(0, page) * 50);
    }

    public List<Map<String, Object>> recentActivity() {
        return jdbc.queryForList("""
                SELECT created_at AS happened, 'USER_REGISTERED' AS action, CONCAT('User #',id) AS subject FROM users
                UNION ALL SELECT updated_at, 'EVENT_UPDATED', event_name FROM events
                UNION ALL SELECT review_date, 'REVIEW_SUBMITTED', CONCAT('Review #',id) FROM review
                ORDER BY happened DESC LIMIT 20
                """);
    }

    public Map<String, Object> counts() {
        return jdbc.queryForMap("SELECT (SELECT COUNT(*) FROM users) AS users, (SELECT COUNT(*) FROM vendors) AS vendors, (SELECT COUNT(*) FROM vendors WHERE approval_status='PENDING') AS pending, (SELECT COUNT(*) FROM bookings) AS bookings, (SELECT COUNT(*) FROM events) AS events");
    }

    private void log(String actor, String action, String target, String detail) {
        jdbc.update("INSERT INTO admin_activity(actor,action,target,details) VALUES(?,?,?,?)", actor, action, target, detail);
    }

    @Transactional
    public void decide(Long id, String status, String reason, String actor) {
        if (!Set.of("APPROVED", "REJECTED").contains(status))
            throw new IllegalArgumentException("Choose approve or reject.");
        reason = reason == null ? "" : reason.trim();
        if (reason.length() > 500 || (status.equals("REJECTED") && reason.isEmpty()))
            throw new IllegalArgumentException("Rejection requires a reason (maximum 500 characters).");
        var rows = jdbc.queryForList("SELECT business_name,approval_status FROM vendors WHERE id=? FOR UPDATE", id);
        if (rows.isEmpty()) throw new IllegalArgumentException("Vendor not found.");
        if (status.equals(rows.getFirst().get("approval_status")))
            throw new IllegalArgumentException("Vendor is already " + status.toLowerCase() + ".");
        jdbc.update("UPDATE vendors SET approval_status=?,approval_reason=? WHERE id=?", status, reason, id);
        log(actor, "VENDOR_" + status, "Vendor #" + id, reason);
    }

    @Transactional
    public void updateUser(Long id, UserUpdateDto dto, String actor) {
        if (dto.getFullName() == null || dto.getFullName().isBlank() || dto.getFullName().length() > 255)
            throw new IllegalArgumentException("Enter a name (maximum 255 characters).");
        if (dto.getEmail() == null || dto.getEmail().length() > 255 || !dto.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new IllegalArgumentException("Enter a valid email address.");
        if (dto.getPhoneNumber() != null && dto.getPhoneNumber().length() > 45)
            throw new IllegalArgumentException("Phone number is too long.");
        if (dto.getPassword() != null && !dto.getPassword().isBlank() && (dto.getPassword().length() < 8 || dto.getPassword().length() > 72))
            throw new IllegalArgumentException("Password must have 8–72 characters.");
        if (users.getUserById(id).getEmail().equals(actor) && !actor.equals(dto.getEmail()))
            throw new IllegalArgumentException("Use another administrator to change your login email.");
        users.updateUser(id, dto);
        log(actor, "USER_UPDATED", "User #" + id, "Profile updated");
    }

    @Transactional
    public void deleteUser(Long id, String actor) {
        var target = users.getUserById(id);
        if (target.getEmail().equals(actor))
            throw new IllegalArgumentException("You cannot delete your own admin account.");
        // Lock admin rows to serialize concurrent removal of administrators.
        var admins = jdbc.queryForList("SELECT id FROM users WHERE role='ROLE_ADMIN' FOR UPDATE");
        if (target.getRole().equals("ROLE_ADMIN") && admins.size() <= 1)
            throw new IllegalArgumentException("The last administrator cannot be deleted.");
        users.deleteUser(id);
        log(actor, "USER_DELETED", "User #" + id, "Account removed");
    }

    @Transactional
    public void updateVendor(Long id, VendorForm form, String actor) {
        vendors.updateProfile(id, form);
        log(actor, "VENDOR_UPDATED", "Vendor #" + id, "Profile updated");
    }

    @Transactional
    public void deleteVendor(Long id, String actor) {
        vendors.getById(id);
        jdbc.update("DELETE FROM vendor_packages WHERE vendor_id=?", id);
        vendors.deleteAccount(id);
        log(actor, "VENDOR_DELETED", "Vendor #" + id, "Account and service packages removed");
    }
}
