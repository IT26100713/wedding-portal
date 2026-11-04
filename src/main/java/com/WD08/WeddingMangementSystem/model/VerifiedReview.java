package com.WD08.WeddingMangementSystem.model;

import java.time.LocalDate;

/**
 * A review written after a completed booking, so the booking id is kept.
 * INHERITANCE: VerifiedReview extends Review and adds one more field, bookingId.
 */
public class VerifiedReview extends Review {

    private int bookingId;

    // Default constructor
    public VerifiedReview() {
        super();
        this.bookingId = 0;
    }

    // Overloaded constructor
    public VerifiedReview(int id, int customerId, int packageId, int bookingId, int rating,
                          String comment, LocalDate date) {
        super(id, customerId, packageId, rating, comment, date);
        setBookingId(bookingId);
    }

    // OVERRIDING: replaces the parent's version, which always returns 0
    @Override
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("Please enter a valid booking id.");
        }
        this.bookingId = bookingId;
    }

    @Override
    public String getType() {
        return "VERIFIED";
    }

    @Override
    public String getLabel(ViewerRole viewer) {
        if (viewer == ViewerRole.ADMIN) {
            return "Verified booking #" + bookingId + " (customer #" + getCustomerId() + ")";
        }
        return "Verified booking";
    }
}
