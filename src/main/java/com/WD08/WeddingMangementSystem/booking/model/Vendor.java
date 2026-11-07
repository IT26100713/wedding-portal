package com.WD08.WeddingMangementSystem.booking.model;

// ABSTRACTION: a Vendor is never created directly. Only its subclasses
// (Photographer, Caterer, Decorator) can be created, and each one decides
// how its own cost is worked out.
public abstract class Vendor {
    // ENCAPSULATION: fields are private, read through getters only
    private int vendorId;
    private String vendorName;
    private double price;

    public Vendor(int vendorId, String vendorName, double price) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.price = price;
    }

    public int getVendorId() {
        return vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public double getPrice() {
        return price;
    }

    // Each subclass must say which service it is: Photography, Catering or Decoration
    public abstract String getServiceType();

    // POLYMORPHISM: the same call gives a different result for each kind of vendor
    public abstract double calculateCost(int guestCount);

    // Short text shown next to the price, e.g. "per event" or "per guest"
    public abstract String getPriceLabel();
}
