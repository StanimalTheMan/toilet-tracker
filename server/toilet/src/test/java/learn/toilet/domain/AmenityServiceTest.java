package learn.toilet.domain;

import learn.toilet.data.AmenityRepository;
import learn.toilet.models.Amenity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class AmenityServiceTest {

    @Autowired
    AmenityService service;

    @MockBean
    AmenityRepository repository;

    @Test
    void shouldFindAll() {
        List<Amenity> expected = List.of(
                new Amenity(1, "soap"),
                new Amenity(2, "handicap stall"),
                new Amenity(3, "water")
        );
        when(repository.findAll()).thenReturn(expected);
        List<Amenity> actual = service.findAll();
        assertEquals(expected, actual);
    }

    @Test
    void shouldFindById() {
        Amenity expected = makeAmenity();
        when(repository.findById(1)).thenReturn(expected);
        Amenity actual = service.findById(1);
        assertEquals(expected, actual);
    }

    @Test
    void shouldFindByName() {
        Amenity expected = makeAmenity();
        when(repository.findByName("air freshener")).thenReturn(expected);
        Amenity actual = service.findByName("air freshener");
        assertEquals(expected, actual);
    }

    @Test
    void shouldNotAddWhenInvalid() {
        // should not add `null`.
        Result<Amenity> result = service.add(null);
        assertEquals(ResultType.INVALID, result.getType());

        // should not add if amenityId is greater than 0.
        Amenity amenity = makeAmenity();
        result = service.add(amenity);
        assertEquals(ResultType.INVALID, result.getType());

        // should not add if name is null.
        amenity = makeAmenity();
        amenity.setAmenityId(0);
        amenity.setAmenityName(null);
        result = service.add(amenity);
        assertEquals(ResultType.INVALID, result.getType());

        // should not add if name is empty.
        amenity = makeAmenity();
        amenity.setAmenityId(0);
        amenity.setAmenityName("");
        result = service.add(amenity);
        assertEquals(ResultType.INVALID, result.getType());

        // should not add if name is duplicate.
        amenity = makeAmenity();
        when(repository.findByName("air freshener")).thenReturn(amenity);
        amenity = makeAmenity();
        amenity.setAmenityId(0);
        amenity.setAmenityName("air freshener");
        result = service.add(amenity);
        assertEquals(ResultType.INVALID, result.getType());
    }

    @Test
    void shouldAddWhenValid() {
        Amenity expected = makeAmenity();
        Amenity amenity = makeAmenity();
        amenity.setAmenityId(0);

        when(repository.add(amenity)).thenReturn(expected);
        Result<Amenity> result = service.add(amenity);
        assertEquals(ResultType.SUCCESS, result.getType());
    }

    @Test
    void shouldNotUpdateWhenInvalid() {
        // should not update `null`.
        Result<Amenity> result = service.update(null);
        assertEquals(ResultType.INVALID, result.getType());

        // should not add if amenityId is 0.
        Amenity amenity = makeAmenity();
        amenity.setAmenityId(0);
        result = service.update(amenity);
        assertEquals(ResultType.INVALID, result.getType());

        // should not update if name is null.
        amenity = makeAmenity();
        amenity.setAmenityName(null);
        result = service.update(amenity);
        assertEquals(ResultType.INVALID, result.getType());

        // should not update if name is empty.
        amenity = makeAmenity();
        amenity.setAmenityName("");
        result = service.update(amenity);
        assertEquals(ResultType.INVALID, result.getType());

        // should not update if duplicate name.
        Amenity existingAmenity = makeAmenity();
        amenity.setAmenityId(1);
        when(repository.findByName("air freshener")).thenReturn(existingAmenity);
        amenity = makeAmenity();
        amenity.setAmenityId(3);
        amenity.setAmenityName("air freshener");
        result = service.update(amenity);
        assertEquals(ResultType.INVALID, result.getType());

        // should not update if not found.
        amenity = makeAmenity();
        amenity.setAmenityId(3);
        amenity.setAmenityName("NA Amenity");
        when(repository.update(amenity)).thenReturn(false);
        result = service.update(amenity);
        assertEquals(ResultType.NOT_FOUND, result.getType());
    }

    @Test
    void shouldUpdateWhenValid() {
        Amenity securityClearance = makeAmenity();
        securityClearance.setAmenityName("Hospitality");

        when(repository.update(securityClearance)).thenReturn(true);
        Result<Amenity> result = service.update(securityClearance);
        assertEquals(ResultType.SUCCESS, result.getType());
    }

    @Test
    void shouldNotDeleteWhenNotFound() {
        when(repository.deleteById(33)).thenReturn(false);
        assertEquals(false, service.deleteById(3));
    }

    private Amenity makeAmenity() {
        Amenity amenity = new Amenity();
        amenity.setAmenityId(1);
        amenity.setAmenityName("air freshener");
        return amenity;
    }
}