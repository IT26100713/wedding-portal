package com.WD08.WeddingMangementSystem.model;

/** The three states of a review. */
public enum ReviewStatus {
    PENDING,   // waiting for the admin
    APPROVED,  // visible to everyone
    HIDDEN     // hidden by the admin
}
