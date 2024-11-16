package learn.toilet.data;

import learn.toilet.models.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReviewJdbcTemplateRepositoryTest {


    @Autowired
    ReviewJdbcTemplateRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }


    @Test
    void shouldFindByUserId()
    {
        System.out.println(repository.findByUserId(2));
        List<Review> reviewsById = repository.findByUserId(2);
        //if we delete we are at 0, if we add first we are at 2
        assertTrue(reviewsById.size() >= 0 && reviewsById.size() <= 2);

    }


    @Test
    void shouldFindByRestroomId()
    {
        System.out.println(repository.findByRestroomId(1));
        List<Review> reviewsById = repository.findByRestroomId(1);
        assertTrue(reviewsById.size() >= 0 && reviewsById.size() <= 2);
    }

    @Test
    void shouldAddReview()
    {
        Review review = new Review();
        System.out.println(repository.findByRestroomId(1));
        review.setReviewText("This bathroom SUCKS!");
        review.setRating(3);
        review.setRestroomId(1);
        Timestamp t = Timestamp.valueOf(LocalDateTime.now());
        review.setTimeStamp(t);
        review.setUsed(LocalDate.now());
        review.setUserId(1);
        Review actual = repository.add(review);

        assertEquals(4, actual.getReviewId());

    }

    @Test
    void shouldUpdate()
    {
        Review review = new Review();
        review.setReviewId(3);
        review.setRating(5);
        review.setReviewText("This bathroom ROCKS!");
        Timestamp t = Timestamp.valueOf(LocalDateTime.now());
        review.setTimeStamp(t);
        review.setUsed(LocalDate.now());
        review.setRestroomId(1);
        review.setUserId(1);

        assertTrue(repository.update(review));
    }

    @Test
    void shouldDelete()
    {
        assertTrue(repository.deleteById(1));
        assertFalse(repository.deleteById(1));
    }


}