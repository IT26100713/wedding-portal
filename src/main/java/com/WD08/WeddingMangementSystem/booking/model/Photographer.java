package com.WD08.WeddingMangementSystem.booking.model;

// INHERITANCE: a Photographer is a Vendor that charges one price for the whole event
public class Photographer extends Vendor {

    public Photographer(int vendorId, String vendorName, double price) {
        super(vendorId, vendorName, price);
    }

    @Override
    public String getServiceType() {
        return "Photography";
    }

    @Override
    public double calculateCost(int guestCount) {
        return getPrice();
    }

    @Override
    public String getPriceLabel() {
        return "per event";
    }
}
