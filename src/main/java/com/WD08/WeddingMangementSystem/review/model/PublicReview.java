package com.WD08.WeddingMangementSystem.review.model;

import java.time.LocalDate;

/**
 * A review from a customer who gives no booking id. It is not verified.
 * INHERITANCE: PublicReview extends Review.
 */
public class PublicReview extends Review {

    // Default constructor
    public PublicReview() {
        super();
    }

    // Overloaded constructor: super(...) passes the values to the parent
    public PublicReview(int id, int customerId, int packageId, int rating, String comment, LocalDate date) {
        super(id, customerId, packageId, rating, comment, date);
    }

    @Override
    public String getType() {
        return "PUBLIC";
    }

    // POLYMORPHISM: customers and the admin see different text
    @Override
    public String getLabel(ViewerRole viewer) {
        if (viewer == ViewerRole.ADMIN) {
            return "Public review (no booking, not verified)";
        }
        return "Public review";
    }
}
