package learn.toilet.domain;

import learn.toilet.data.ReviewRepository;
import learn.toilet.models.Review;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public Review findById(int reviewId) {
        return repository.findById(reviewId);
    }

    public List<Review> findByUserId(int userId) {
        return repository.findByUserId(userId);
    }

    public List<Review> findByRestroomId(int restroomId) {
        return repository.findByRestroomId(restroomId);
    }

    public Result<Review> add(Review review) {

        // Generate a timestamp
        if (review.getTimeStamp() == null) {
            review.setTimeStamp(Timestamp.valueOf(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()));
        }


        Result<Review> result = validate(review);

        if(!result.isSuccess()) {
            return result;
        }

        if(review.getReviewId() != 0){
            result.addMessage("reviewId must be set for `add` operation", ResultType.INVALID);
            return result;
        }


        review = repository.add(review);
        result.setPayload(review);
        return result;
    }

    public Result<Review> update(Review review) {

        // Generate a timestamp
        if (review.getTimeStamp() == null) {
            review.setTimeStamp(Timestamp.valueOf(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()));
        }

        Result<Review> result = validate(review);
        if(!result.isSuccess()) {
            return result;
        }

        if(review.getReviewId() <= 0) {
            result.addMessage("reviewId must be set for `update` operation", ResultType.INVALID);
            return result;
        }

        if(!repository.update(review)) {
            result.addMessage("reviewId: " + review.getReviewId() + " not found", ResultType.NOT_FOUND);
        }

        return result;
    }

    public boolean deleteById(int reviewId) {
        return repository.deleteById(reviewId);
    }

    public Result<Review> validate(Review review) {
        Result<Review> result = new Result<>();

        if (review == null) {
            result.addMessage("Review Cannot Be NULL", ResultType.INVALID);
            return result;
        }

        if(review.getRating() > 5 || review.getRating() < 1)
        {
            result.addMessage("Rating must be from 1 to 5", ResultType.INVALID);
        }

        if(Validations.isNullOrBlank(review.getReviewText())) {
            result.addMessage("Review Body Is Required", ResultType.INVALID);
        }

        if (review.getUsed() != null && review.getUsed().isAfter(LocalDate.now())) {
            result.addMessage("Date Used must be in the past", ResultType.INVALID);
        } else if (review.getUsed() == null) {
            result.addMessage("Date Used cannot be null", ResultType.INVALID);
        }

        if (review.getTimeStamp() == null) {
            result.addMessage("Timestamp Used cannot be null", ResultType.INVALID);
        }

        return result;


    }
}
