package com.WD08.WeddingMangementSystem.vendor.service;

import com.WD08.WeddingMangementSystem.vendor.dto.VendorForm;
import com.WD08.WeddingMangementSystem.vendor.exception.VendorException;
import com.WD08.WeddingMangementSystem.vendor.model.Vendor;
import com.WD08.WeddingMangementSystem.vendor.model.VendorFactory;
import com.WD08.WeddingMangementSystem.vendor.repository.VendorRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class VendorService {
    private static final List<String> CATEGORIES =
            List.of("CATERING", "PHOTOGRAPHY", "VENUE", "DECORATION");

    private final VendorRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public VendorService(VendorRepository repo) {
        this.repo = repo;
    }

    public List<String> getCategories() {
        return CATEGORIES;
    }

    // ---------- CREATE ----------
    public Vendor register(VendorForm form) {
        if (form.getCategory() == null || !CATEGORIES.contains(form.getCategory())) {
            throw new VendorException("Please choose a valid category.");
        }
        validateProfile(form, null);
        if (form.getPassword() == null || form.getPassword().length() < 6) {
            throw new VendorException("Password must be at least 6 characters.");
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new VendorException("Passwords do not match.");
        }

        Vendor vendor = VendorFactory.create(form.getCategory());   // right subclass
        copyFields(form, vendor);
        vendor.setPasswordHash(encoder.encode(form.getPassword()));
        Long newId = repo.save(vendor);
        vendor.setId(newId);
        return vendor;
    }

    // ---------- LOGIN ----------
    public Vendor login(String email, String password) {
        String cleaned = clean(email).toLowerCase();
        Vendor vendor = repo.findByEmail(cleaned)
                .orElseThrow(() -> new VendorException("Invalid email or password."));
        if (password == null || !encoder.matches(password, vendor.getPasswordHash())) {
            throw new VendorException("Invalid email or password.");
        }
        return vendor;
    }

    // ---------- READ ----------
    public Vendor getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new VendorException("Vendor not found."));
    }

    public VendorForm toForm(Vendor v) {
        VendorForm f = new VendorForm();
        f.setBusinessName(v.getBusinessName());
        f.setOwnerName(v.getOwnerName());
        f.setEmail(v.getEmail());
        f.setPhone(v.getPhone());
        f.setCity(v.getCity());
        f.setCategory(v.getCategory());
        f.setDescription(v.getDescription());
        f.setBaseRate(v.getBaseRate());
        return f;
    }

    // ---------- UPDATE ----------
    public void updateProfile(Long vendorId, VendorForm form) {
        Vendor vendor = getById(vendorId);
        validateProfile(form, vendorId);
        copyFields(form, vendor);
        repo.update(vendor);
    }

    // ---------- DELETE ----------
    public void deleteAccount(Long vendorId) {
        if (repo.delete(vendorId) == 0) {
            throw new VendorException("Vendor not found.");
        }
    }

    // ---------- POLYMORPHISM in action ----------
    public double calculateQuote(Long vendorId, int units) {
        Vendor vendor = getById(vendorId);     // could be any subclass
        return vendor.calculateQuote(units);   // runs the subclass's version
    }

    // ---------- helpers ----------
    private void validateProfile(VendorForm f, Long currentId) {
        String business = clean(f.getBusinessName());
        String owner = clean(f.getOwnerName());
        String email = clean(f.getEmail()).toLowerCase();
        String phone = clean(f.getPhone());
        String city = clean(f.getCity());
        String desc = clean(f.getDescription());

        if (business.isEmpty() || business.length() > 100) {
            throw new VendorException("Business name is required (max 100 characters).");
        }
        if (owner.isEmpty() || owner.length() > 100) {
            throw new VendorException("Owner name is required (max 100 characters).");
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new VendorException("Please enter a valid email address.");
        }
        if (!phone.matches("^[0-9+\\- ]{9,15}$")) {
            throw new VendorException("Phone must be 9-15 digits (you may use + - and spaces).");
        }
        if (city.isEmpty() || city.length() > 60) {
            throw new VendorException("City is required (max 60 characters).");
        }
        if (f.getBaseRate() < 0) {
            throw new VendorException("Base rate cannot be negative.");
        }
        if (desc.length() > 500) {
            throw new VendorException("Description must be 500 characters or less.");
        }

        boolean taken = (currentId == null)
                ? repo.existsByEmail(email)
                : repo.existsByEmailAndIdNot(email, currentId);
        if (taken) {
            throw new VendorException("That email is already registered.");
        }
    }

    private void copyFields(VendorForm f, Vendor v) {
        v.setBusinessName(clean(f.getBusinessName()));
        v.setOwnerName(clean(f.getOwnerName()));
        v.setEmail(clean(f.getEmail()).toLowerCase());
        v.setPhone(clean(f.getPhone()));
        v.setCity(clean(f.getCity()));
        v.setDescription(clean(f.getDescription()));
        v.setBaseRate(f.getBaseRate());
    }

    private String clean(String s) {
        return s == null ? "" : s.trim();
    }

}
