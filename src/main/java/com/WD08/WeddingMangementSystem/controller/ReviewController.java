package com.WD08.WeddingMangementSystem.controller;

import com.WD08.WeddingMangementSystem.exception.ReviewException;
import com.WD08.WeddingMangementSystem.model.Review;
import com.WD08.WeddingMangementSystem.model.ViewerRole;
import com.WD08.WeddingMangementSystem.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * These are the API URLs the front end calls (http://localhost:8080/api/...).
 * The controller only receives the request and sends the answer.
 * The rules are in ReviewService and the database work is done by JdbcReviewStore.
 *
 * Demo only: the customer id comes from the request and the admin URLs are open.
 * The real login (User part) must protect them later.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")   // lets the pages opened from IntelliJ (another port) call this server
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    // =====================  Customer URLs  =====================

    // GET /api/reviews?packageId=1     approved reviews of a package + average rating
    // GET /api/reviews?customerId=12   all reviews of one customer
    @GetMapping("/reviews")
    public Map<String, Object> listReviews(
            @RequestParam(name = "packageId", required = false) Integer packageId,
            @RequestParam(name = "customerId", required = false) Integer customerId) {
        if (packageId != null) {
            List<Review> list = service.getApprovedReviews(packageId);
            Map<String, Object> answer = success(null);
            answer.put("count", list.size());
            answer.put("averageRating", service.calculateAverageRating(list));
            answer.put("reviews", toJson(list, ViewerRole.CUSTOMER));
            return answer;
        }
        if (customerId != null) {
            List<Review> list = service.getReviewsByCustomer(customerId);
            Map<String, Object> answer = success(null);
            answer.put("count", list.size());
            answer.put("reviews", toJson(list, ViewerRole.CUSTOMER));
            return answer;
        }
        throw new ReviewException(HttpStatus.BAD_REQUEST, "Please give packageId or customerId.");
    }

    // GET /api/reviews/5
    @GetMapping("/reviews/{id}")
    public Map<String, Object> getReview(@PathVariable("id") int id) {
        Map<String, Object> answer = success(null);
        answer.put("review", toJson(service.findReview(id), ViewerRole.CUSTOMER));
        return answer;
    }

    // POST /api/reviews    body: customerId, packageId, bookingId, rating, comment, bookingComplete
    @PostMapping("/reviews")
    public ResponseEntity<Map<String, Object>> addReview(@RequestBody ReviewRequest body) {
        // The Booking part will tell us if the booking is complete. Until then the front end sends "true".
        boolean bookingComplete = !"false".equals(body.getBookingComplete());
        Review saved = service.addReview(toInt(body.getCustomerId()), toInt(body.getPackageId()),
                toInt(body.getBookingId()), toInt(body.getRating()), body.getComment(), bookingComplete);
        Map<String, Object> answer = success("Thank you! Your review was sent and will appear after the team approves it.");
        answer.put("review", toJson(saved, ViewerRole.CUSTOMER));
        return ResponseEntity.status(HttpStatus.CREATED).body(answer);
    }

    // PUT /api/reviews/5    body: customerId, rating, comment
    @PutMapping("/reviews/{id}")
    public Map<String, Object> updateReview(@PathVariable("id") int id, @RequestBody ReviewRequest body) {
        service.updateReview(toInt(body.getCustomerId()), id, toInt(body.getRating()), body.getComment());
        return success("Your review was updated and sent for approval again.");
    }

    // DELETE /api/reviews/5?customerId=12
    @DeleteMapping("/reviews/{id}")
    public Map<String, Object> deleteOwnReview(@PathVariable("id") int id,
                                               @RequestParam(name = "customerId") int customerId) {
        service.deleteOwnReview(customerId, id);
        return success("Your review was deleted.");
    }

    // =====================  Admin URLs  =====================

    // GET /api/admin/reviews?status=PENDING&rating=2&packageId=1   (every filter is optional)
    @GetMapping("/admin/reviews")
    public Map<String, Object> adminList(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "packageId", required = false) Integer packageId) {
        List<Review> list = service.searchReviews(status, rating, packageId);
        Map<String, Object> answer = success(null);
        answer.put("count", list.size());
        answer.put("reviews", toJson(list, ViewerRole.ADMIN));
        return answer;
    }

    @PostMapping("/admin/reviews/{id}/approve")
    public Map<String, Object> approve(@PathVariable("id") int id) {
        service.approve(id);
        return success("Review approved. It is now public.");
    }

    @PostMapping("/admin/reviews/{id}/hide")
    public Map<String, Object> hide(@PathVariable("id") int id) {
        service.hide(id);
        return success("Review hidden.");
    }

    // POST /api/admin/reviews/5/reply    body: reply
    @PostMapping("/admin/reviews/{id}/reply")
    public Map<String, Object> reply(@PathVariable("id") int id, @RequestBody ReviewRequest body) {
        service.reply(id, body.getReply());
        return success("Reply saved.");
    }

    @DeleteMapping("/admin/reviews/{id}")
    public Map<String, Object> adminDelete(@PathVariable("id") int id) {
        service.adminDelete(id);
        return success("Review deleted.");
    }

    // =====================  Errors  =====================

    // Every ReviewException from the service becomes {"success":false,"message":"..."}
    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<Map<String, Object>> handleReviewException(ReviewException e) {
        Map<String, Object> answer = new LinkedHashMap<String, Object>();
        answer.put("success", false);
        answer.put("message", e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(answer);
    }

    // =====================  Helpers  =====================

    private Map<String, Object> success(String message) {
        Map<String, Object> answer = new LinkedHashMap<String, Object>();
        answer.put("success", true);
        if (message != null) {
            answer.put("message", message);
        }
        return answer;
    }

    // Review object -> the JSON shape the front end expects.
    // POLYMORPHISM: r.getType() and r.getLabel(viewer) run the code of PublicReview or VerifiedReview,
    // and the label is different for a customer and for the admin.
    private Map<String, Object> toJson(Review r, ViewerRole viewer) {
        Map<String, Object> json = new LinkedHashMap<String, Object>();
        json.put("id", r.getId());
        json.put("customerId", r.getCustomerId());
        json.put("packageId", r.getPackageId());
        json.put("bookingId", r.getBookingId());
        json.put("rating", r.getRating());
        json.put("comment", r.getComment());
        json.put("date", r.getDate().toString());
        json.put("status", r.getStatus().name());
        json.put("reply", r.getReplyText());
        json.put("type", r.getType());
        json.put("label", r.getLabel(viewer));
        return json;
    }

    private List<Map<String, Object>> toJson(List<Review> reviews, ViewerRole viewer) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < reviews.size(); i++) {
            list.add(toJson(reviews.get(i), viewer));
        }
        return list;
    }

    // Text -> number. Returns 0 when it is missing or not a number.
    private int toInt(String text) {
        if (text == null) {
            return 0;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
