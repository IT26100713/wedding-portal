package com.WD08.WeddingMangementSystem.model;

import java.time.LocalDate;

/**
 * The admin's reply to a review.
 * COMPOSITION: a Review owns its AdminReply. A reply cannot exist without its review.
 */
public class AdminReply {

    private String replyText;
    private LocalDate replyDate;

    // Default constructor
    public AdminReply() {
        this.replyText = "";
        this.replyDate = LocalDate.now();
    }

    // Overloaded constructor
    public AdminReply(String replyText, LocalDate replyDate) {
        setReplyText(replyText);
        setReplyDate(replyDate);
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        if (replyText == null || replyText.trim().length() == 0 || replyText.trim().length() > 500) {
            throw new IllegalArgumentException("The reply must have 1 to 500 characters.");
        }
        this.replyText = replyText.trim();
    }

    public LocalDate getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(LocalDate replyDate) {
        if (replyDate == null) {
            throw new IllegalArgumentException("The reply date is missing.");
        }
        this.replyDate = replyDate;
    }
}
