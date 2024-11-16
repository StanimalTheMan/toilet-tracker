package learn.toilet.data;


import learn.toilet.data.mappers.RestroomMapper;
import org.springframework.stereotype.Repository;



import learn.toilet.data.mappers.AmenityMapper;
import learn.toilet.models.Amenity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class AmenityJdbcTemplateRepository implements AmenityRepository {

    private final JdbcTemplate jdbcTemplate;

    public AmenityJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Amenity> findAll() {
        final String sql = "select amenity_id, amenity_name "
                + "from amenity;";
        return jdbcTemplate.query(sql, new AmenityMapper());
    }

    @Override
    public Amenity findById(int amenityId) {

        final String sql = "select amenity_id, amenity_name "
                + "from amenity "
                + "where amenity_id = ?;";
        return jdbcTemplate.query(sql, new AmenityMapper(), amenityId).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Amenity findByName(String amenityName) {
        final String sql = "select amenity_id, amenity_name "
                + "from amenity "
                + "where lower(amenity_name) = lower(?);";
        return jdbcTemplate.query(sql, new AmenityMapper(), amenityName).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Amenity add(Amenity amenity) {
        final String sql = "insert into amenity (amenity_name)"
                + "values (?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, amenity.getAmenityName());
            return ps;
        }, keyHolder);

        if (rowsAffected <= 0) {
            return null;
        }

        amenity.setAmenityId(keyHolder.getKey().intValue());
        return amenity;
    }

    @Override
    public boolean update(Amenity amenity) {
        final String sql = "update amenity set "
                + "amenity_name = ? "
                + "where amenity_id = ?;";

        return jdbcTemplate.update(sql,
                amenity.getAmenityName(),
                amenity.getAmenityId()) > 0;
    }


    @Override
    public boolean deleteById(int amenityId) {
        jdbcTemplate.update("delete from restroom_amenity where amenity_id = ?;", amenityId);
        return jdbcTemplate.update(
                "delete from amenity where amenity_id = ?", amenityId) > 0;
    }
}