package learn.toilet.data;

import learn.toilet.models.RestroomAmenity;
import learn.toilet.models.Amenity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RestroomAmenityJdbcTemplateRepositoryTest {

    @Autowired
    RestroomAmenityJdbcTemplateRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldAdd() {
        RestroomAmenity agencyAmenity = makeRestroomAmenity();
        assertTrue(repository.add(agencyAmenity));

        try {
            repository.add(agencyAmenity); // must fail
            fail("cannot add an amenity to a restroom twice.");
        } catch (DataAccessException ex) {
            // this is expected.
        }
    }

    @Test
    void shouldDelete() {
        assertTrue(repository.deleteByKey(1, 1));
        assertFalse(repository.deleteByKey(1, 1));
    }

    RestroomAmenity makeRestroomAmenity() {
        RestroomAmenity restroomAmenity = new RestroomAmenity();
        restroomAmenity.setRestroomId(1);

        Amenity amenity = new Amenity();
        amenity.setAmenityId(3);
        amenity.setAmenityName("Test");
        restroomAmenity.setAmenity(amenity);
        return restroomAmenity;
    }
}