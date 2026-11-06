package com.WD08.WeddingMangementSystem.review.model;

import java.time.LocalDate;

/**
 * A star rating and a comment about a package.
 *
 * ABSTRACTION: this class is abstract, so you cannot write "new Review()". You create a
 *              PublicReview or a VerifiedReview, which both extend (inherit from) Review.
 * ENCAPSULATION / INFORMATION HIDING: every field is private. The only way to change a field
 *              is through its setter, and the setter checks the value first.
 * POLYMORPHISM: getType() and getLabel(viewer) are abstract. Each child class answers in its own
 *              way, and the program calls them on a Review without knowing which child it is.
 */
public abstract class Review {

    private int id;
    private int customerId;
    private int packageId;
    private int rating;
    private String comment;
    private LocalDate date;
    private ReviewStatus status;
    private AdminReply adminReply;     // composition, or null when the admin has not replied

    // Default constructor (protected: only the child classes can call it)
    protected Review() {
        this.id = 0;
        this.date = LocalDate.now();
        this.status = ReviewStatus.PENDING;
        this.adminReply = null;
    }

    // Overloaded constructor: the setters check every value
    protected Review(int id, int customerId, int packageId, int rating, String comment, LocalDate date) {
        this();
        setId(id);
        setCustomerId(customerId);
        setPackageId(packageId);
        setRating(rating);
        setComment(comment);
        setDate(date);
    }

    // ---------- Getters ----------
    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getPackageId() {
        return packageId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public AdminReply getAdminReply() {
        return adminReply;
    }

    // A public review has no booking, so this returns 0. VerifiedReview overrides it.
    public int getBookingId() {
        return 0;
    }

    public String getReplyText() {
        if (adminReply == null) {
            return "";
        }
        return adminReply.getReplyText();
    }

    // ---------- Setters (with validation) ----------
    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("The review id cannot be negative.");
        }
        this.id = id;
    }

    public void setCustomerId(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("The customer id is missing.");
        }
        this.customerId = customerId;
    }

    public void setPackageId(int packageId) {
        if (packageId <= 0) {
            throw new IllegalArgumentException("Please enter a package id.");
        }
        this.packageId = packageId;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Please choose a rating from 1 to 5.");
        }
        this.rating = rating;
    }

    public void setComment(String comment) {
        if (comment == null || comment.trim().length() < 10 || comment.trim().length() > 500) {
            throw new IllegalArgumentException("The comment must have 10 to 500 characters.");
        }
        this.comment = comment.trim();
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("The date is missing.");
        }
        this.date = date;
    }

    public void setStatus(ReviewStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("The status is missing.");
        }
        this.status = status;
    }

    // ---------- Behaviour ----------
    public void approve() {
        this.status = ReviewStatus.APPROVED;
    }

    public void hide() {
        this.status = ReviewStatus.HIDDEN;
    }

    public boolean isApproved() {
        return status == ReviewStatus.APPROVED;
    }

    // Composition: the AdminReply object is created here, inside the Review
    public void addReply(String replyText) {
        this.adminReply = new AdminReply(replyText, LocalDate.now());
    }

    // Used when a review is read back from the file
    public void restoreReply(String replyText, LocalDate replyDate) {
        this.adminReply = new AdminReply(replyText, replyDate);
    }

    // ---------- Abstract methods: every child class MUST write its own version ----------
    public abstract String getType();

    public abstract String getLabel(ViewerRole viewer);
}
