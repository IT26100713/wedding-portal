package com.WD08.WeddingMangementSystem.controller;

/**
 * The JSON the front end sends, for example {"customerId":"12","rating":"5","comment":"..."}.
 * Everything is a String and is turned into numbers in the controller,
 * so a wrong or empty value gives a friendly message instead of a crash.
 */
public class ReviewRequest {

    private String customerId;
    private String packageId;
    private String bookingId;
    private String bookingComplete;
    private String rating;
    private String comment;
    private String reply;

    public ReviewRequest() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingComplete() {
        return bookingComplete;
    }

    public void setBookingComplete(String bookingComplete) {
        this.bookingComplete = bookingComplete;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
