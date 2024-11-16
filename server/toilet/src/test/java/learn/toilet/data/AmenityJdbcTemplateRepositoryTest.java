package learn.toilet.data;

import learn.toilet.models.Amenity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AmenityJdbcTemplateRepositoryTest {

    final static int NEXT_AMENITY_ID = 4;

    @Autowired
    AmenityJdbcTemplateRepository repository;

    @Autowired
    KnownGoodState knownGoodState;

    @BeforeEach
    void setup() {
        knownGoodState.set();
    }

    @Test
    void shouldFindById() {
        Amenity actual = repository.findById(1);
        assertNotNull(actual);
        assertEquals(1, actual.getAmenityId());
        assertEquals("soap", actual.getAmenityName());
    }

    @Test
    void shouldFindByName() {
        Amenity actual = repository.findByName("handicap stall");
        assertNotNull(actual);
        assertEquals(2, actual.getAmenityId());
    }

    @Test
    void shouldAdd() {
        Amenity amenity = makeAmenity();
        Amenity actual = repository.add(amenity);
        assertNotNull(actual);
        assertEquals(NEXT_AMENITY_ID, actual.getAmenityId());
    }

    @Test
    void shouldUpdate() {
        Amenity amenity = makeAmenity();
        amenity.setAmenityId(1);
        assertTrue(repository.update(amenity));
        amenity.setAmenityId(15);
        assertFalse(repository.update(amenity));
    }

    @Test
    void shouldDelete() {
        assertTrue(repository.deleteById(2));
        assertFalse(repository.deleteById(2));
    }

    Amenity makeAmenity() {
        Amenity amenity = new Amenity();
        amenity.setAmenityName("Test Amenity");
        return amenity;
    }
}