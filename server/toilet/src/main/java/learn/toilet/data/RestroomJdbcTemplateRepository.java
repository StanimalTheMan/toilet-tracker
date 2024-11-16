package learn.toilet.data;

import learn.toilet.data.mappers.RestroomAmenityMapper;
import learn.toilet.data.mappers.RestroomMapper;
import learn.toilet.data.mappers.ReviewMapper;
import learn.toilet.models.Restroom;
import learn.toilet.data.RestroomRepository;
import learn.toilet.models.Review;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class RestroomJdbcTemplateRepository implements RestroomRepository {

    private final JdbcTemplate jdbcTemplate;

    public RestroomJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Restroom> findAll() {
        // limit until we develop a paging solution
        final String sql = "select restroom_id, `name`, address, latitude, longitude, directions, `description`, app_user_id from restroom limit 1000;";
        return jdbcTemplate.query(sql, new RestroomMapper());
    }

    @Override
    @Transactional
    public Restroom findById(int restroomId) {

        final String sql = "select restroom_id, `name`, address, latitude, longitude, directions, `description`, app_user_id "
                + "from restroom "
                + "where restroom_id = ?;";

        Restroom result = jdbcTemplate.query(sql, new RestroomMapper(), restroomId).stream()
                .findAny().orElse(null);

        if (result != null) {
            addReviews(result);
            addAmenities(result);
        }

        return result;
    }

    @Override
    public List<Restroom> findByUserId(int userId)
    {
        final String sql = "select restroom_id, `name`, address, latitude, longitude, directions, `description`, app_user_id "
                + "from restroom "
                + "where app_user_id = ?;";

        return jdbcTemplate.query(sql, new RestroomMapper(), userId);
    }

    @Override
    public Restroom add(Restroom restroom) {

        final String sql = "insert into restroom (name, address, latitude, longitude, directions, description, app_user_id) values (?,?,?,?,?,?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, restroom.getName());
            ps.setString(2, restroom.getAddress());
            ps.setDouble(3, restroom.getLatitude());
            ps.setDouble(4, restroom.getLongitude());
            ps.setString(5, restroom.getDirections());
            ps.setString(6, restroom.getDescription());
            ps.setInt(7, restroom.getUserId());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        restroom.setRestroomId(keyHolder.getKey().intValue());
        return restroom;
    }

    @Override
    public boolean update(Restroom restroom) {

        final String sql = "update restroom set "
                + "name = ?, "
                + "address = ?, "
                + "latitude = ?, "
                + "longitude = ?, "
                + "directions = ?, "
                + "description = ? "
                + "where restroom_id = ?;";

        return jdbcTemplate.update(sql, restroom.getName(), restroom.getAddress(), restroom.getLatitude(), restroom.getLongitude(), restroom.getDirections(), restroom.getDescription(), restroom.getRestroomId()) > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(int restroomId) {
        jdbcTemplate.update("delete from review where restroom_id = ?", restroomId);
        jdbcTemplate.update("delete from restroom_amenity where restroom_id = ?", restroomId);
        return jdbcTemplate.update("delete from restroom where restroom_id = ?", restroomId) > 0;
    }

    private void addReviews(Restroom restroom) {

        final String sql = "select r.review_id, r.rating, r.review_text, r.timestamp, r.date_used, r.restroom_id, "
                + "r.app_user_id, u.username "
                + "from review r "
                + "inner join app_user u on r.app_user_id = u.app_user_id "
                + "where r.restroom_id = ?";

        var reviews = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Review review = new Review();
            review.setReviewId(rs.getInt("review_id"));
            review.setRating(rs.getInt("rating"));
            review.setReviewText(rs.getString("review_text"));
            review.setTimeStamp(rs.getTimestamp("timestamp"));
            review.setUsed(rs.getDate("date_used").toLocalDate());
            review.setRestroomId(rs.getInt("restroom_id"));
            review.setUserId(rs.getInt("app_user_id"));
            review.setUsername(rs.getString("username")); // Set the username directly
            return review;
        }, restroom.getRestroomId());

        restroom.setReviews(reviews);
    }

    private void addAmenities(Restroom restroom) {

        final String sql = "select ra.restroom_id, ra.amenity_id, "
                + "a.amenity_name "
                + "from restroom_amenity ra "
                + "inner join amenity a on ra.amenity_id = a.amenity_id "
                + "where ra.restroom_id = ?";

        var restroomAmenities = jdbcTemplate.query(sql, new RestroomAmenityMapper(), restroom.getRestroomId());
        restroom.setAmenities(restroomAmenities);
    }

}
