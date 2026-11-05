package com.WD08.WeddingMangementSystem.repository;

import com.WD08.WeddingMangementSystem.exception.ReviewException;
import com.WD08.WeddingMangementSystem.model.PublicReview;
import com.WD08.WeddingMangementSystem.model.Review;
import com.WD08.WeddingMangementSystem.model.ReviewStatus;
import com.WD08.WeddingMangementSystem.model.VerifiedReview;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DATABASE CONNECTIVITY with plain JDBC.
 *
 * Spring creates the DataSource from the settings in application.properties
 * (spring.datasource.url, username, password) and gives it to this class.
 * Every method does the same 4 things:
 *   1. get a Connection from the DataSource
 *   2. prepare the SQL in a PreparedStatement (the ? marks are filled in safely, so SQL injection is not possible)
 *   3. run it (executeQuery for SELECT, executeUpdate for INSERT / UPDATE / DELETE)
 *   4. turn each row (ResultSet) into a PublicReview or VerifiedReview object
 * try-with-resources closes the Connection, the PreparedStatement and the ResultSet automatically.
 *
 * The table "review" (one row per review, review_type tells which class to create):
 *   id, review_type, customer_id, package_id, booking_id, rating, comment_text,
 *   review_date, status, reply_text, reply_date
 */
@Repository
public class JdbcReviewStore implements ReviewStore {

    private static final String COLUMNS =
            "id, review_type, customer_id, package_id, booking_id, rating, comment_text, review_date, status, reply_text, reply_date";

    private final DataSource dataSource;

    public JdbcReviewStore(DataSource dataSource) {
        this.dataSource = dataSource;
        createTableIfMissing();            // so the project also works when schema.sql was not run
    }

    // =====================  Read  =====================

    @Override
    public Review findById(int id) {
        List<Review> list = query("SELECT " + COLUMNS + " FROM review WHERE id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Review> findByCustomerId(int customerId) {
        return query("SELECT " + COLUMNS + " FROM review WHERE customer_id = ? ORDER BY review_date DESC, id DESC", customerId);
    }

    @Override
    public List<Review> findApprovedByPackageId(int packageId) {
        return search(ReviewStatus.APPROVED, null, packageId);
    }

    @Override
    public List<Review> search(ReviewStatus status, Integer rating, Integer packageId) {
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNS + " FROM review WHERE 1 = 1");
        List<Object> values = new ArrayList<Object>();
        if (status != null) {
            sql.append(" AND status = ?");
            values.add(status.name());
        }
        if (rating != null && rating > 0) {
            sql.append(" AND rating = ?");
            values.add(rating);
        }
        if (packageId != null && packageId > 0) {
            sql.append(" AND package_id = ?");
            values.add(packageId);
        }
        sql.append(" ORDER BY review_date DESC, id DESC");
        return query(sql.toString(), values.toArray());
    }

    @Override
    public boolean existsByBookingId(int bookingId) {
        return count("SELECT COUNT(*) FROM review WHERE booking_id = ?", bookingId) > 0;
    }

    @Override
    public boolean existsPublicReview(int customerId, int packageId) {
        return count("SELECT COUNT(*) FROM review WHERE review_type = 'PUBLIC' AND customer_id = ? AND package_id = ?",
                customerId, packageId) > 0;
    }

    // =====================  Create and Update  =====================

    @Override
    public Review save(Review review) {
        try (Connection con = dataSource.getConnection()) {
            if (review.getId() == 0) {
                String sql = "INSERT INTO review (review_type, customer_id, package_id, booking_id, rating, comment_text, "
                        + "review_date, status, reply_text, reply_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, review.getType());
                    bindFields(ps, review, 2);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            review.setId(keys.getInt(1));
                        }
                    }
                }
            } else {
                String sql = "UPDATE review SET customer_id = ?, package_id = ?, booking_id = ?, rating = ?, comment_text = ?, "
                        + "review_date = ?, status = ?, reply_text = ?, reply_date = ? WHERE id = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    bindFields(ps, review, 1);
                    ps.setInt(10, review.getId());
                    ps.executeUpdate();
                }
            }
            return review;
        } catch (SQLIntegrityConstraintViolationException e) {
            // the UNIQUE rule of the table: one review per booking
            throw new ReviewException(HttpStatus.BAD_REQUEST, "You have already reviewed this booking.");
        } catch (SQLException e) {
            throw databaseError(e);
        }
    }

    // =====================  Delete  =====================

    @Override
    public boolean delete(int id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM review WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw databaseError(e);
        }
    }

    // =====================  Helpers  =====================

    // Runs a SELECT and turns every row into a Review object
    private List<Review> query(String sql, Object... values) {
        List<Review> list = new ArrayList<Review>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw databaseError(e);
        }
        return list;
    }

    private int count(String sql, Object... values) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw databaseError(e);
        }
    }

    // Puts the 9 changeable fields of a review into the ? marks, starting at position "first"
    private void bindFields(PreparedStatement ps, Review r, int first) throws SQLException {
        int i = first;
        ps.setInt(i++, r.getCustomerId());
        ps.setInt(i++, r.getPackageId());
        if (r.getBookingId() > 0) {
            ps.setInt(i++, r.getBookingId());
        } else {
            ps.setNull(i++, Types.INTEGER);          // a public review has no booking
        }
        ps.setInt(i++, r.getRating());
        ps.setString(i++, r.getComment());
        ps.setObject(i++, r.getDate());
        ps.setString(i++, r.getStatus().name());
        if (r.getAdminReply() == null) {
            ps.setNull(i++, Types.VARCHAR);
            ps.setNull(i++, Types.DATE);
        } else {
            ps.setString(i++, r.getAdminReply().getReplyText());
            ps.setObject(i++, r.getAdminReply().getReplyDate());
        }
    }

    // One row of the table -> one PublicReview or VerifiedReview object
    private Review mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int customerId = rs.getInt("customer_id");
        int packageId = rs.getInt("package_id");
        int bookingId = rs.getInt("booking_id");            // a NULL in the table becomes 0
        int rating = rs.getInt("rating");
        String comment = rs.getString("comment_text");
        LocalDate date = rs.getObject("review_date", LocalDate.class);

        Review review;
        if (rs.getString("review_type").equals("VERIFIED")) {
            review = new VerifiedReview(id, customerId, packageId, bookingId, rating, comment, date);
        } else {
            review = new PublicReview(id, customerId, packageId, rating, comment, date);
        }
        review.setStatus(ReviewStatus.valueOf(rs.getString("status")));
        String replyText = rs.getString("reply_text");
        if (replyText != null) {
            review.restoreReply(replyText, rs.getObject("reply_date", LocalDate.class));
        }
        return review;
    }

    private void createTableIfMissing() {
        String sql = "CREATE TABLE IF NOT EXISTS review ("
                + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "review_type VARCHAR(10) NOT NULL, "
                + "customer_id INT NOT NULL, "
                + "package_id INT NOT NULL, "
                + "booking_id INT NULL, "
                + "rating INT NOT NULL, "
                + "comment_text VARCHAR(500) NOT NULL, "
                + "review_date DATE NOT NULL, "
                + "status VARCHAR(20) NOT NULL, "
                + "reply_text VARCHAR(500) NULL, "
                + "reply_date DATE NULL, "
                + "CONSTRAINT uq_review_booking UNIQUE (booking_id), "
                + "CONSTRAINT ck_review_rating CHECK (rating BETWEEN 1 AND 5)"
                + ") ENGINE = InnoDB DEFAULT CHARSET = utf8mb4";
        try (Connection con = dataSource.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw databaseError(e);
        }
    }

    private ReviewException databaseError(SQLException e) {
        return new ReviewException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
    }
}
