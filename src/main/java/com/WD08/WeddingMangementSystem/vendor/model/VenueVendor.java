package com.WD08.WeddingMangementSystem.vendor.model;

public class VenueVendor extends Vendor {

    @Override
    public String getCategory() {
        return "VENUE";
    }

    @Override
    public String getPricingLabel() {
        return "per event (flat rate)";
    }

    @Override
    public double calculateQuote(int units) {
        return getBaseRate();   // flat price, units ignored
    }
}
