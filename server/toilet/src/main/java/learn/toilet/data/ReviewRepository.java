package learn.toilet.data;

import learn.toilet.models.Review;

import java.util.List;

public interface ReviewRepository {
    Review findById(int reviewId);

    List<Review> findByUserId(int userId);

    List<Review> findByRestroomId(int restroomId);

    Review add(Review review);

    boolean update(Review review);

    boolean deleteById(int reviewId);

}
