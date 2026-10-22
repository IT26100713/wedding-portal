package com.WD08.WeddingMangementSystem.booking.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.WD08.WeddingMangementSystem.booking.model.Caterer;
import com.WD08.WeddingMangementSystem.booking.model.Decorator;
import com.WD08.WeddingMangementSystem.booking.model.Photographer;
import com.WD08.WeddingMangementSystem.booking.model.Vendor;

// The vendors a couple can choose from: 4 photographers, 4 caterers and 4 decorators.
// The list holds Vendor objects, but each one is really a Photographer, Caterer or
// Decorator (POLYMORPHISM: one list, different kinds of objects).
@Service
public class VendorService {
    private List<Vendor> vendors = new ArrayList<Vendor>();

    public VendorService() {
        vendors.add(new Photographer(1, "Lens Studio", 75000));
        vendors.add(new Photographer(2, "Golden Frame", 90000));
        vendors.add(new Photographer(3, "Snap Moments", 60000));
        vendors.add(new Photographer(4, "Helen's Studio", 80000));
        vendors.add(new Caterer(5, "Royal Caterers", 2500));      // per guest
        vendors.add(new Caterer(6, "Spice Garden", 2000));
        vendors.add(new Caterer(7, "Taste of Lanka", 1800));
        vendors.add(new Caterer(8, "Hilton Caterers", 3000));
        vendors.add(new Decorator(9, "Bloom Decor", 120000));
        vendors.add(new Decorator(10, "Elegant Events", 150000));
        vendors.add(new Decorator(11, "Petal Touch", 100000));
        vendors.add(new Decorator(12, "Charming Events", 180000));
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

    // Cost of a vendor for this many guests, or 0 if no vendor was chosen.
    // calculateCost() runs the Photographer, Caterer or Decorator version (polymorphism).
    public double costOf(String name, String serviceType, int guestCount) {
        Vendor v = findVendor(name, serviceType);
        if (v == null) {
            return 0;
        }
        return v.calculateCost(guestCount);
    }
}
