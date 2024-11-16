package learn.toilet.data;

import learn.toilet.models.Restroom;
import learn.toilet.models.RestroomAmenity;

import java.util.List;

public interface RestroomAmenityRepository {
    boolean add(RestroomAmenity restroomAmenity);

    List<RestroomAmenity> findByRestroomId(int restroomId);

    boolean deleteByKey(int restroomId, int amenityId);
}
