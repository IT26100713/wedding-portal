package com.WD08.WeddingMangementSystem.vendor.model;

public class DecorationVendor extends Vendor {

    @Override
    public String getCategory() {
        return "DECORATION";
    }

    @Override
    public String getPricingLabel() {
        return "per day";
    }

    @Override
    public double calculateQuote(int units) {
        return getBaseRate() * units;
    }
}
