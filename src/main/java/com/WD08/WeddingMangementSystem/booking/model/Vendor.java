package com.WD08.WeddingMangementSystem.booking.model;

// A photographer, caterer or decorator that a couple can book.
// (Later these can come from the Vendor Management module instead of VendorService.)
public class Vendor {
    private int vendorId;
    private String vendorName;
    private String serviceType;   // Photography, Catering or Decoration
    private double price;

    // Parameterized constructor
    public Vendor(int vendorId, String vendorName, String serviceType, double price) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.serviceType = serviceType;
        this.price = price;
    }

    public int getVendorId() {
        return vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public double getPrice() {
        return price;
    }
}
