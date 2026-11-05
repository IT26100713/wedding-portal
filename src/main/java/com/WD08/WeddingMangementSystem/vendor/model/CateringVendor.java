package com.WD08.WeddingMangementSystem.vendor.model;

public class CateringVendor extends Vendor {

    @Override
    public String getCategory() {
        return "CATERING";
    }

    @Override
    public String getPricingLabel() {
        return "per guest";
    }

    @Override
    public double calculateQuote(int units) {
        double total = getBaseRate() * units;
        if (units >= 200) {
            total = total * 0.95;   // 5% discount for big weddings
        }
        return total;
    }



}
