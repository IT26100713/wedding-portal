package com.WD08.WeddingMangementSystem.booking.model;

// INHERITANCE: a Caterer is a Vendor that charges for every guest (price per plate)
public class Caterer extends Vendor {

    public Caterer(int vendorId, String vendorName, double pricePerPlate) {
        super(vendorId, vendorName, pricePerPlate);
    }

    @Override
    public String getServiceType() {
        return "Catering";
    }

    @Override
    public double calculateCost(int guestCount) {
        return getPrice() * guestCount;
    }

    @Override
    public String getPriceLabel() {
        return "per guest";
    }
}
