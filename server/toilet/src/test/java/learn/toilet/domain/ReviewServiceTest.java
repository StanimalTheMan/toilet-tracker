package learn.toilet.domain;
import learn.toilet.data.ReviewRepository;
import learn.toilet.models.Restroom;
import learn.toilet.models.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest()
public class ReviewServiceTest {

    @Autowired
    ReviewService service;

    @MockBean
    ReviewRepository reviewRepository;


    @Test
    void shouldAdd()
    {
        Timestamp t = Timestamp.valueOf(LocalDateTime.now());
        Review review = new Review(0, 1, "TEST", t, LocalDate.now(), 1, 1);
        Review mockOut = new Review(4, 1, "TEST", t, LocalDate.now(), 1, 1);

        when(reviewRepository.add(review)).thenReturn(mockOut);

        Result<Review> actual = service.add(review);
        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockOut, actual.getPayload());
    }

    @Test
    void shouldNotAddWhenInvalid()
    {
        Timestamp t = Timestamp.valueOf(LocalDateTime.now());
        Review review = new Review(14, 1, "TEST", t, LocalDate.now(), 1, 1);

        //reviewId must not be set when adding
        Result<Review> actual = service.add(review);
        assertEquals(actual.getType(), ResultType.INVALID);

        //rating must be from 1 to 5
        review.setReviewId(0);
        review.setRating(0);
        actual = service.add(review);
        assertEquals(actual.getType(), ResultType.INVALID);

        // date must be in the past
        review.setRating(1);
        review.setUsed(LocalDate.now().plusDays(2));
        actual = service.add(review);
        assertEquals(actual.getType(), ResultType.INVALID);

        // review text cant be empty
        review.setUsed(LocalDate.now().minusDays(2));
        review.setReviewText("");
        actual = service.update(review);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldUpdate()
    {
        Timestamp t = Timestamp.valueOf(LocalDateTime.now());
        Review review = new Review(1, 1, "TEST", t, LocalDate.now(), 1, 1);

        when(reviewRepository.update(review)).thenReturn(true);

        Result<Review> actual = service.update(review);
        assertEquals(ResultType.SUCCESS, actual.getType());

    }

    @Test
    void shouldNotUpdateWhenMissing()
    {
        Timestamp t = Timestamp.valueOf(LocalDateTime.now());
        Review review = new Review(100, 1, "TEST", t, LocalDate.now(), 1, 1);

        when(reviewRepository.update(review)).thenReturn(false);

        Result<Review> actual = service.update(review);
        assertEquals(ResultType.NOT_FOUND, actual.getType());

    }

    @Test
    void shouldNotUpdateWhenInvalid()
    {
        Timestamp t = Timestamp.valueOf(LocalDateTime.now());
        Review review = new Review(1, 1, "TEST", t, LocalDate.now(), 1, 1);

        review.setRating(0);
        Result<Review> actual = service.update(review);
        assertEquals(ResultType.INVALID, actual.getType());

        review.setRating(1);
        review.setReviewText("");
        actual = service.update(review);
        assertEquals(ResultType.INVALID, actual.getType());

        review.setReviewText("TEST");
        review.setUsed(LocalDate.now().plusDays(2));
        actual = service.add(review);
        assertEquals(actual.getType(), ResultType.INVALID);

    }

    @Test
    void shouldNotDeleteWhenNotFound() {
        assertFalse(service.deleteById(12));
    }

    @Test
    void shouldDelete() {
        when(reviewRepository.deleteById(1)).thenReturn(true);
        assertTrue(service.deleteById(1));
    }

}
