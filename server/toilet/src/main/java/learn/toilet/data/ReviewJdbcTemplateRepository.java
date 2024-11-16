package learn.toilet.data;

import learn.toilet.data.mappers.ReviewMapper;
import learn.toilet.models.Review;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Repository
public class ReviewJdbcTemplateRepository implements ReviewRepository {

    private final JdbcTemplate jdbcTemplate;


    public ReviewJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review findById(int reviewId) {
        final String sql = "select review_id, rating, review_text, timestamp, date_used, restroom_id, app_user_id "
                + "from review "
                + "where review_id = ?;";

        return jdbcTemplate.query(sql, new ReviewMapper(), reviewId).stream().findFirst().orElse(null);
    }


    @Override
    public List<Review> findByUserId(int userId) {
        final String sql = "select review_id, rating, review_text, timestamp, date_used, r.restroom_id, r.app_user_id, rr.name "
                + "from review r "
                + "inner join restroom rr on rr.restroom_id = r.restroom_id "
                + "where r.app_user_id = ?;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Review review = new Review();
            review.setReviewId(rs.getInt("review_id"));
            review.setRating(rs.getInt("rating"));
            review.setReviewText(rs.getString("review_text"));
            review.setTimeStamp(rs.getTimestamp("timestamp"));
            review.setUsed(rs.getDate("date_used").toLocalDate());
            review.setRestroomId(rs.getInt("restroom_id"));
            review.setUserId(rs.getInt("app_user_id"));
            review.setLocationName(rs.getString("name"));
            return review;
        }, userId);
    }

    @Override
    public List<Review> findByRestroomId(int restroomId) {
        final String sql = "select review_id, rating, review_text, timestamp, date_used, restroom_id, app_user_id "
                + "from review "
                + "where restroom_id = ?;";

        return jdbcTemplate.query(sql, new ReviewMapper(), restroomId);
    }


    @Override
    public Review add(Review review) {
        final String sql = "insert into review (rating, review_text, timestamp, date_used, restroom_id, app_user_id)"
                + " values (?,?,?,?,?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
//        System.out.println("Rating: " + review.getRating());
//        System.out.println("Review Text: " + review.getReviewText());
//        System.out.println("Timestamp: " + review.getTimeStamp());
//        System.out.println("Used Date: " + review.getUsed());
//        System.out.println("Restroom ID: " + review.getRestroomId());
//        System.out.println("User ID: " + review.getUserId());
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getReviewText());
            ps.setTimestamp(3, review.getTimeStamp());
            ps.setDate(4, Date.valueOf(review.getUsed()));
            ps.setInt(5, review.getRestroomId());
            ps.setInt(6, review.getUserId());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        review.setReviewId(keyHolder.getKey().intValue());
        return review;

    }

    @Transactional
    @Override
    public boolean update(Review review) {

        final String sql = "update review set "
                + "rating = ?, "
                + "review_text = ?, "
                + "timestamp = ?, "
                + "date_used = ?, "
                + "restroom_id = ?, "
                + "app_user_id = ? "
                + "where review_id = ?;";

        return jdbcTemplate.update(sql,
                review.getRating(),
                review.getReviewText(),
                review.getTimeStamp(),
                review.getUsed(),
                review.getRestroomId(),
                review.getUserId(),
                review.getReviewId()) > 0;
    }

    @Transactional
    @Override
    public boolean deleteById(int reviewId) {

        final String sql = "delete from review where review_id = ?;";

        return jdbcTemplate.update(sql, reviewId) > 0;
    }


}



