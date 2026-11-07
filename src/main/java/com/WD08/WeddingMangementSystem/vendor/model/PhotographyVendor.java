package com.WD08.WeddingMangementSystem.vendor.model;

public class PhotographyVendor extends Vendor {

    @Override
    public String getCategory() {
        return "PHOTOGRAPHY";
    }

    @Override
    public String getPricingLabel() {
        return "per hour (+10% equipment fee)";
    }

    @Override
    public double calculateQuote(int units) {
        return getBaseRate() * units * 1.10;
    }


}
