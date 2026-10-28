package com.WD08.WeddingMangementSystem.vendor.service;

import com.WD08.WeddingMangementSystem.vendor.exception.VendorException;
import com.WD08.WeddingMangementSystem.vendor.model.VendorPackage;
import com.WD08.WeddingMangementSystem.vendor.repository.PackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class PackageService {

    private final PackageRepository repo;

    public PackageService(PackageRepository repo) {
        this.repo = repo;
    }

    public List<VendorPackage> listForVendor(Long vendorId) {
        return repo.findByVendorId(vendorId);
    }

    public VendorPackage getOwned(Long packageId, Long vendorId) {
        return repo.findByIdAndVendorId(packageId, vendorId)
                .orElseThrow(() -> new VendorException("Package not found."));
    }

    public void create(Long vendorId, VendorPackage p) {
        validate(p);
        p.setVendorId(vendorId);
        p.setName(p.getName().trim());
        repo.save(p);
    }

    public void update(Long vendorId, VendorPackage p) {
        validate(p);
        p.setVendorId(vendorId);
        p.setName(p.getName().trim());
        if (repo.update(p) == 0) {
            throw new VendorException("Package not found.");
        }
    }

    public void delete(Long packageId, Long vendorId) {
        if (repo.delete(packageId, vendorId) == 0) {
            throw new VendorException("Package not found.");
        }
    }

    private void validate(VendorPackage p) {
        String name = p.getName() == null ? "" : p.getName().trim();
        if (name.isEmpty() || name.length() > 100) {
            throw new VendorException("Package name is required (max 100 characters).");
        }
        if (p.getPrice() <= 0) {
            throw new VendorException("Price must be greater than 0.");
        }
        if (p.getDescription() != null && p.getDescription().length() > 300) {
            throw new VendorException("Description must be 300 characters or less.");
        }
    }

}
