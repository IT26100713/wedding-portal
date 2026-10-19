package com.WD08.WeddingMangementSystem.vendor.model;

public final class VendorFactory {
    private VendorFactory() {
    }

    public static Vendor create(String category) {
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        switch (category.toUpperCase()) {
            case "CATERING":
                return new CateringVendor();
            case "PHOTOGRAPHY":
                return new PhotographyVendor();
            case "VENUE":
                return new VenueVendor();
            case "DECORATION":
                return new DecorationVendor();
            default:
                throw new IllegalArgumentException("Unknown category: " + category);
        }
    }
}
