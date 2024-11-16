package learn.toilet.domain;

import learn.toilet.data.AppUserRepository;
import learn.toilet.data.RestroomAmenityJdbcTemplateRepository;
import learn.toilet.data.RestroomAmenityRepository;
import learn.toilet.data.RestroomRepository;
import learn.toilet.models.Amenity;
import learn.toilet.models.AppUser;
import learn.toilet.models.Restroom;
import learn.toilet.models.RestroomAmenity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class RestroomServiceTest {

    @Autowired
    RestroomService service;

    @MockBean
    RestroomRepository restroomRepository;

    @MockBean
    AppUserRepository appUserRepository;

    @MockBean
    RestroomAmenityRepository restroomAmenityRepository;

    @Test
    void shouldFindAll() {
        when(restroomRepository.findAll()).thenReturn(
                List.of(
                        new Restroom(1, "bathroom", 40.748817, -73.985428, "10 apple street", "down the hall", "disgusting", 1)
                )
        );

        List<Restroom> actual = service.findAll();

        assertEquals(1, actual.size());
        verify(restroomRepository).findAll();
    }

    @Test
    void findByLocation_returnsRestroomsWithin5Miles() {
        List<Restroom> restrooms = List.of(
                new Restroom(1, "Restroom A", 40.7128, -74.0060, "Test", "Test", "Test", 1), // within 5 miles
                new Restroom(2, "Restroom B", 40.7808, -73.9772, "Test", "Test", "Test", 1), // within 5 miles
                new Restroom(3, "Restroom C", 41.0000, -75.0000, "Test", "Test", "Test", 1)  // outside 5 miles
        );
        when(restroomRepository.findAll()).thenReturn(restrooms);

        double latitude = 40.7306;
        double longitude = -73.9352;
        List<Restroom> result = service.findByLocation(latitude, longitude);
        List<String> names = result.stream().map(Restroom::getName).collect(Collectors.toList());

        assertEquals(2, names.size());
        assertTrue(names.contains("Restroom A"));
        assertTrue(names.contains("Restroom B"));
        assertFalse(names.contains("Restroom C"));
    }

    @Test
    void shouldFindById() {
        Restroom expected = new Restroom(1, "bathroom", 40.748817, -73.985428, "10 apple street", "down the hall", "disgusting", 1);
        when(restroomRepository.findById(1)).thenReturn(expected);
        Restroom actual = service.findById(1);
        assertEquals(expected, actual);
    }

    @Test
    void shouldAdd() {
        Restroom restroom = new Restroom(0, "TEST", 0.0, 0.0, "TEST", "TEST", "TEST", 1);
        Restroom mockOut = new Restroom(3, "TEST", 0.0, 0.0, "TEST", "TEST", "TEST", 1);
        AppUser user = new AppUser(1, "1234", "$2a$10$QO8UzE8TDb1N6BQDwMTPGeV6HMYhgeffflkj4vwZ0jxDrhplKP8Yq", true, List.of("admin"));

        when(appUserRepository.findById(1)).thenReturn(user);
        when(restroomRepository.add(restroom)).thenReturn(mockOut);

        Result<Restroom> actual = service.add(restroom);
        System.out.println(actual.getMessages());
        assertEquals(ResultType.SUCCESS, actual.getType());
        assertEquals(mockOut, actual.getPayload());
    }

    @Test
    void shouldNotAddWhenInvalid() {

        Restroom restroom = new Restroom(35, "TEST", 0.0, 0.0, "TEST", "TEST", "TEST", 1);

        Result<Restroom> actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setRestroomId(0);
        restroom.setName(null);
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setName("");
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setName("TEST");
        restroom.setAddress(null);
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setAddress("");
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setAddress("TEST");
        restroom.setDescription(null);
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setDescription("");
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setDescription("TEST");
        restroom.setDirections(null);
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setDirections("");
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setDirections("TEST");
        restroom.setLatitude(-91);
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setLatitude(91);
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setLatitude(0);
        restroom.setLongitude(-181);
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setLongitude(181);
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setLongitude(0);
        restroom.setUserId(0);
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setLongitude(0);
        restroom.setUserId(4);
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldUpdate() {
        Restroom restroom = new Restroom(1, "TEST", 0.0, 0.0, "TEST", "TEST", "TEST", 1);
        AppUser user = new AppUser(1, "1234", "$2a$10$QO8UzE8TDb1N6BQDwMTPGeV6HMYhgeffflkj4vwZ0jxDrhplKP8Yq", true, List.of("admin"));
        when(appUserRepository.findById(1)).thenReturn(user);
        when(restroomRepository.update(restroom)).thenReturn(true);
        Result<Restroom> actual = service.update(restroom);
        assertEquals(ResultType.SUCCESS, actual.getType());
    }

    @Test
    void shouldNotUpdateMissing() {
        Restroom restroom = new Restroom(35, "TEST", 0.0, 0.0, "TEST", "TEST", "TEST", 1);
        when(restroomRepository.update(restroom)).thenReturn(false);
        Result<Restroom> actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotUpdateWhenInvalid() {
        Restroom restroom = new Restroom(1, "TEST", 0.0, 0.0, "TEST", "TEST", "TEST", 1);

        restroom.setName(null);
        Result<Restroom> actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setName("");
        actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setName("TEST");
        restroom.setAddress(null);
        actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setAddress("");
        actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setAddress("TEST");
        restroom.setDescription(null);
        actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setDescription("");
        actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setDescription("TEST");
        restroom.setDirections(null);
        actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setDirections("");
        actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setDirections("TEST");
        restroom.setLatitude(-91);
        actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setLatitude(91);
        actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setLatitude(0);
        restroom.setLongitude(-181);
        actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setLongitude(181);
        actual = service.update(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setLongitude(0);
        restroom.setUserId(0);
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());

        restroom.setLongitude(0);
        restroom.setUserId(4);
        actual = service.add(restroom);
        assertEquals(ResultType.INVALID, actual.getType());
    }

    @Test
    void shouldNotDeleteMissing() {
        Result<Restroom> result = service.deleteById(1024);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertTrue(result.getMessages().get(0).contains("not found"));
    }

    @Test
    void shouldDelete() {
        when(restroomRepository.deleteById(1)).thenReturn(true);
        Result<Restroom> result = service.deleteById(1);
        assertTrue(result.isSuccess());
        verify(restroomRepository).deleteById(any(int.class));
    }

    @Test
    void shouldAddRestroomAmenity(){
        RestroomAmenity restroomAmenity = new RestroomAmenity();
        restroomAmenity.setRestroomId(1);
        Amenity amenity = new Amenity();
        restroomAmenity.setAmenity(amenity);
        when(restroomAmenityRepository.add(restroomAmenity)).thenReturn(true);
        Result<Void> result = service.addAmenity(restroomAmenity);
        assertTrue(result.isSuccess());
        verify(restroomAmenityRepository).add(restroomAmenity);
    }

    @Test
    void shouldNotAddInvalidRestroomAmenity(){
        RestroomAmenity restroomAmenity = new RestroomAmenity();
        restroomAmenity.setRestroomId(0);
        Amenity amenity = new Amenity();
        restroomAmenity.setAmenity(amenity);
        Result<Void> result = service.addAmenity(restroomAmenity);
        assertFalse(result.isSuccess());

        restroomAmenity.setRestroomId(1);
        restroomAmenity.setAmenity(null);
        result = service.addAmenity(restroomAmenity);
        assertFalse(result.isSuccess());

        result = service.addAmenity(null);
        assertFalse(result.isSuccess());
    }


    @Test
    void shouldDeleteRestroomAmenity(){
        int restroomId = 1;
        int amenityId = 2;

        when(restroomAmenityRepository.deleteByKey(restroomId, amenityId)).thenReturn(true);
        boolean result = service.deleteAmenityByKey(restroomId, amenityId);
        assertTrue(result);
        verify(restroomAmenityRepository).deleteByKey(restroomId, amenityId);
    }

    @Test
    void shouldNotDeleteMissingRestroomAmenity() {
        int restroomId = 1;
        int amenityId = 2;

        when(restroomAmenityRepository.deleteByKey(restroomId, amenityId)).thenReturn(false);
        boolean result = service.deleteAmenityByKey(restroomId, amenityId);
        assertFalse(result);
        verify(restroomAmenityRepository).deleteByKey(restroomId, amenityId);
    }
}