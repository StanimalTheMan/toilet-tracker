package learn.toilet.data;

import learn.toilet.models.Restroom;
import learn.toilet.models.Review;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RestroomRepository {
    List<Restroom> findAll();

    @Transactional
    Restroom findById(int restroomId);

    List<Restroom> findByUserId(int userId);

    Restroom add(Restroom restroom);

    boolean update(Restroom restroom);

    @Transactional
    boolean deleteById(int restroomId);
}
