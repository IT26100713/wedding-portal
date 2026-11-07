package com.WD08.WeddingMangementSystem.review.repository;

import com.WD08.WeddingMangementSystem.review.model.Review;
import com.WD08.WeddingMangementSystem.review.model.ReviewStatus;

import java.util.List;

/**
 * ABSTRACTION: this interface says WHAT the storage can do, not HOW.
 * The service only knows this interface. Today JdbcReviewStore saves in MySQL.
 * A text-file class (or any other storage) could implement the same interface and nothing else would change.
 */
public interface ReviewStore {

    // Returns null when there is no review with this id
    Review findById(int id);

    List<Review> findByCustomerId(int customerId);

    // Only APPROVED reviews of one package (what the public sees)
    List<Review> findApprovedByPackageId(int packageId);

    // Admin search. A null (or 0) filter means "do not filter on this".
    List<Review> search(ReviewStatus status, Integer rating, Integer packageId);

    // Rules: one review per booking, and one public review per customer for each package
    boolean existsByBookingId(int bookingId);

    boolean existsPublicReview(int customerId, int packageId);

    // A review with id 0 is added (it gets a new id). A review with an id is updated.
    Review save(Review review);

    // Returns true when a review was deleted
    boolean delete(int id);
}
