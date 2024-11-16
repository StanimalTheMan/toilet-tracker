package learn.toilet.data;

import learn.toilet.data.mappers.RestroomAmenityMapper;
import learn.toilet.data.mappers.RestroomMapper;
import learn.toilet.models.RestroomAmenity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RestroomAmenityJdbcTemplateRepository implements RestroomAmenityRepository {

    private final JdbcTemplate jdbcTemplate;

    public RestroomAmenityJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RestroomAmenity> findByRestroomId(int restroomId) {
        // Query the database to find all amenities for the given restroomId
        final String sql = "select ra.restroom_id, ra.amenity_id, a.amenity_name "
                + "from restroom_amenity ra "
                + "join amenity a on ra.amenity_id = a.amenity_id "
                + "where ra.restroom_id = ?;";

        return jdbcTemplate.query(sql, new RestroomAmenityMapper(), restroomId);
    }

    @Override
    public boolean add(RestroomAmenity restroomAmenity) {

        final String sql = "insert into restroom_amenity (restroom_id, amenity_id)"
                + " values "
                + "(?,?);";

        return jdbcTemplate.update(sql,
                restroomAmenity.getRestroomId(),
                restroomAmenity.getAmenity().getAmenityId()) > 0;
    }

    @Override
    public boolean deleteByKey(int restroomId, int amenityId) {

        final String sql = "delete from restroom_amenity "
                + "where restroom_id = ? and amenity_id = ?;";

        return jdbcTemplate.update(sql, restroomId, amenityId) > 0;
    }
}
