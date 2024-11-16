package learn.toilet.data.mappers;

import learn.toilet.models.RestroomAmenity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RestroomAmenityMapper implements RowMapper<RestroomAmenity> {
    @Override
    public RestroomAmenity mapRow(ResultSet resultSet, int i) throws SQLException {

        RestroomAmenity restroomAmenity = new RestroomAmenity();
        restroomAmenity.setRestroomId(resultSet.getInt("restroom_id"));

        AmenityMapper amenityMapper = new AmenityMapper();
        restroomAmenity.setAmenity(amenityMapper.mapRow(resultSet, i));

        return restroomAmenity;
    }
}
