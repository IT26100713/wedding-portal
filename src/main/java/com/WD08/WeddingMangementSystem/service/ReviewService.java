package com.WD08.WeddingMangementSystem.service;

import com.WD08.WeddingMangementSystem.exception.ReviewException;
import com.WD08.WeddingMangementSystem.model.PublicReview;
import com.WD08.WeddingMangementSystem.model.Review;
import com.WD08.WeddingMangementSystem.model.ReviewStatus;
import com.WD08.WeddingMangementSystem.model.VerifiedReview;
import com.WD08.WeddingMangementSystem.repository.ReviewStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * All the business rules live here (the "control class" of Lecture 03):
 *  - a review with a booking id is a VerifiedReview and the booking must be complete
 *  - one review per booking, and one public review per customer for each package
 *  - a customer can change or delete only his own review
 *  - every new or edited review waits for the admin (PENDING)
 * The service talks to the storage only through the ReviewStore interface (MySQL today).
 */
@Service
public class ReviewService {

    private final ReviewStore store;

    public ReviewService(ReviewStore store) {
        this.store = store;
    }

    // =====================  Customer  =====================

    // bookingId 0 means "no booking": the review becomes a PublicReview
    public Review addReview(int customerId, int packageId, int bookingId, int rating,
                            String comment, boolean bookingComplete) {
        if (bookingId < 0) {
            throw bad("Please enter a valid booking id.");
        }
        Review review;
        try {
            if (bookingId > 0) {
                if (!bookingComplete) {
                    throw bad("You can use a booking id only after the booking is complete.");
                }
                if (store.existsByBookingId(bookingId)) {
                    throw bad("You have already reviewed this booking.");
                }
                VerifiedReview verified = new VerifiedReview();
                verified.setBookingId(bookingId);
                review = verified;
            } else {
                if (store.existsPublicReview(customerId, packageId)) {
                    throw bad("You have already written a public review for this package.");
                }
                review = new PublicReview();
            }
            // each setter checks its own value and throws IllegalArgumentException when it is wrong
            review.setCustomerId(customerId);
            review.setPackageId(packageId);
            review.setRating(rating);
            review.setComment(comment);
            review.setDate(LocalDate.now());
            review.setStatus(ReviewStatus.PENDING);
        } catch (IllegalArgumentException e) {
            throw bad(e.getMessage());
        }
        return store.save(review);
    }

    // A customer edits his own review. It goes back to PENDING for the admin.
    public Review updateReview(int customerId, int reviewId, int rating, String comment) {
        Review review = findOwnReview(customerId, reviewId);
        try {
            review.setRating(rating);
            review.setComment(comment);
        } catch (IllegalArgumentException e) {
            throw bad(e.getMessage());
        }
        review.setStatus(ReviewStatus.PENDING);
        return store.save(review);
    }

    public void deleteOwnReview(int customerId, int reviewId) {
        findOwnReview(customerId, reviewId);
        store.delete(reviewId);
    }

    public Review findReview(int reviewId) {
        Review review = store.findById(reviewId);
        if (review == null) {
            throw notFound("That review was not found.");
        }
        return review;
    }

    public List<Review> getReviewsByCustomer(int customerId) {
        return store.findByCustomerId(customerId);
    }

    // What the public sees: APPROVED reviews of one package
    public List<Review> getApprovedReviews(int packageId) {
        return store.findApprovedByPackageId(packageId);
    }

    public double calculateAverageRating(List<Review> reviews) {
        if (reviews.size() == 0) {
            return 0;
        }
        int total = 0;
        for (int i = 0; i < reviews.size(); i++) {
            total = total + reviews.get(i).getRating();
        }
        return (double) total / reviews.size();
    }

    // =====================  Admin  =====================

    // A null (or empty) filter means "do not filter on this"
    public List<Review> searchReviews(String status, Integer rating, Integer packageId) {
        ReviewStatus wanted = null;
        if (status != null && status.trim().length() > 0) {
            try {
                wanted = ReviewStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw bad("Unknown status. Use PENDING, APPROVED or HIDDEN.");
            }
        }
        return store.search(wanted, rating, packageId);
    }

    public void approve(int reviewId) {
        Review review = findReview(reviewId);
        review.approve();
        store.save(review);
    }

    public void hide(int reviewId) {
        Review review = findReview(reviewId);
        review.hide();
        store.save(review);
    }

    public void reply(int reviewId, String replyText) {
        Review review = findReview(reviewId);
        try {
            review.addReply(replyText);
        } catch (IllegalArgumentException e) {
            throw bad(e.getMessage());
        }
        store.save(review);
    }

    public void adminDelete(int reviewId) {
        findReview(reviewId);
        store.delete(reviewId);
    }

    // =====================  Helpers  =====================

    private Review findOwnReview(int customerId, int reviewId) {
        Review review = store.findById(reviewId);
        if (review == null || review.getCustomerId() != customerId) {
            throw notFound("That review was not found.");
        }
        return review;
    }

    private ReviewException bad(String message) {
        return new ReviewException(HttpStatus.BAD_REQUEST, message);
    }

    private ReviewException notFound(String message) {
        return new ReviewException(HttpStatus.NOT_FOUND, message);
    }
}
