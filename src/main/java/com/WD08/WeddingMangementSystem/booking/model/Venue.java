package com.WD08.WeddingMangementSystem.booking.model;

// INHERITANCE: a Venue is a Vendor (a hall or hotel) that charges a hall fee
// plus a small amount for every guest
public class Venue extends Vendor {
    private static final double PER_GUEST = 500;

    public Venue(int vendorId, String vendorName, double price) {
        super(vendorId, vendorName, price);
    }

    @Override
    public String getServiceType() {
        return "Venue";
    }

    @Override
    public double calculateCost(int guestCount) {
        return getPrice() + PER_GUEST * guestCount;
    }

    @Override
    public String getPriceLabel() {
        return "hall fee + Rs.500 per guest";
    }
}
