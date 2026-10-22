package com.WD08.WeddingMangementSystem.booking.model;

// INHERITANCE: a Decorator is a Vendor with a fixed price, plus 10,000 for
// every extra 100 guests above the first 100 (bigger hall = more decoration)
public class Decorator extends Vendor {
    private static final int GUESTS_INCLUDED = 100;
    private static final double EXTRA_PER_100_GUESTS = 10000;

    public Decorator(int vendorId, String vendorName, double price) {
        super(vendorId, vendorName, price);
    }

    @Override
    public String getServiceType() {
        return "Decoration";
    }

    @Override
    public double calculateCost(int guestCount) {
        int extraGuests = Math.max(0, guestCount - GUESTS_INCLUDED);
        int extraHundreds = (extraGuests + 99) / 100;   // round up
        return getPrice() + extraHundreds * EXTRA_PER_100_GUESTS;
    }

    @Override
    public String getPriceLabel() {
        return "up to 100 guests";
    }
}
