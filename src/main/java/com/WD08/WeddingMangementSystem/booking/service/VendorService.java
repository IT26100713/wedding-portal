package com.WD08.WeddingMangementSystem.booking.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.WD08.WeddingMangementSystem.booking.model.Vendor;

// The vendors a couple can choose from: 4 photographers, 4 caterers and 4 decorators
@Service
public class VendorService {
    private List<Vendor> vendors = new ArrayList<Vendor>();

    public VendorService() {
        vendors.add(new Vendor(1, "Lens Studio", "Photography", 75000));
        vendors.add(new Vendor(2, "Golden Frame", "Photography", 90000));
        vendors.add(new Vendor(3, "Snap Moments", "Photography", 60000));
        vendors.add(new Vendor(4, "Helen's Studio", "Photography", 80000));
        vendors.add(new Vendor(5, "Royal Caterers", "Catering", 250000));
        vendors.add(new Vendor(6, "Spice Garden", "Catering", 200000));
        vendors.add(new Vendor(7, "Taste of Lanka", "Catering", 180000));
        vendors.add(new Vendor(8, "Hilton Caterers", "Catering", 300000));
        vendors.add(new Vendor(9, "Bloom Decor", "Decoration", 120000));
        vendors.add(new Vendor(10, "Elegant Events", "Decoration", 150000));
        vendors.add(new Vendor(11, "Petal Touch", "Decoration", 100000));
        vendors.add(new Vendor(12, "Charming Events", "Decoration", 180000));
    }

    // Returns all vendors of one service type, e.g. "Photography"
    public List<Vendor> getVendors(String serviceType) {
        List<Vendor> result = new ArrayList<Vendor>();
        for (Vendor v : vendors) {
            if (v.getServiceType().equals(serviceType)) {
                result.add(v);
            }
        }
        return result;
    }

    // Returns the vendor with this name and service type, or null if there is none
    public Vendor findVendor(String name, String serviceType) {
        for (Vendor v : vendors) {
            if (v.getVendorName().equals(name) && v.getServiceType().equals(serviceType)) {
                return v;
            }
        }
        return null;
    }

    // Price of a vendor, or 0 if no vendor was chosen
    public double priceOf(String name, String serviceType) {
        Vendor v = findVendor(name, serviceType);
        if (v == null) {
            return 0;
        }
        return v.getPrice();
    }
}
