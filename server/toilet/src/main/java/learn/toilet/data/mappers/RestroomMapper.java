package learn.toilet.data.mappers;

import learn.toilet.models.Restroom;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RestroomMapper implements RowMapper<Restroom> {

    @Override
    public Restroom mapRow(ResultSet resultSet, int i) throws SQLException {
        Restroom restroom = new Restroom();
        restroom.setRestroomId(resultSet.getInt("restroom_id"));
        restroom.setName(resultSet.getString("name"));
        restroom.setAddress(resultSet.getString("address"));
        restroom.setDescription(resultSet.getString("description"));
        restroom.setDirections(resultSet.getString("directions"));
        restroom.setLatitude(resultSet.getDouble("latitude"));
        restroom.setLongitude(resultSet.getDouble("longitude"));
        restroom.setUserId(resultSet.getInt("app_user_id"));
        return restroom;
    }
}
