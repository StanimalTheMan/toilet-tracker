package learn.toilet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.toilet.data.AmenityRepository;
import learn.toilet.data.AppUserRepository;
import learn.toilet.models.Amenity;
import learn.toilet.models.AppUser;
import learn.toilet.models.Review;
import learn.toilet.security.JwtConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AmenityControllerTest {

    @MockBean
    AmenityRepository amenityRepository;

    @MockBean
    AppUserRepository appUserRepository;

    @Autowired
    MockMvc mvc;

    @Autowired
    JwtConverter jwtConverter;

    String token;

    @BeforeEach
    void setup() {
        AppUser appUser = new AppUser(1, "johndoe@gmailcom", "P@sswOrd!", false,
                List.of("ADMIN"));

        when(appUserRepository.findByUsername("johndoe@gmail.com")).thenReturn(appUser);

        token = jwtConverter.getTokenFromUser(appUser);
    }

    @Test
    void addShouldReturn400WhenEmpty() throws Exception {

        var request = post("/api/amenity/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addShouldReturn400WhenInvalid() throws Exception {

        ObjectMapper jsonMapper = new ObjectMapper();

        Amenity amenity = new Amenity();
        String amenityJson = jsonMapper.writeValueAsString(amenity);

        var request = post("/api/amenity/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(amenityJson);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void appShouldReturn415WhenMultipart() throws Exception {

        ObjectMapper jsonMapper = new ObjectMapper();

        Amenity amenity = new Amenity(0, "New Amenity");
        String amenityJson = jsonMapper.writeValueAsString(amenity);

        var request = post("/api/amenity/admin")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token)
                .content(amenityJson);

        mvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void addShouldReturn201() throws Exception {

        Amenity amenity = new Amenity(0, "New Amenity");
        Amenity expected = new Amenity(1, "New Amenity");

        when(amenityRepository.add(any())).thenReturn(expected);
        ObjectMapper jsonMapper = new ObjectMapper();

        String amenityJson = jsonMapper.writeValueAsString(amenity);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        var request = post("/api/amenity/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(amenityJson);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }


    @Test
    void updateShouldReturn204NoContent() throws Exception {
        Amenity amenity = new Amenity(1, "Updated Amenity");


        when(amenityRepository.update(any())).thenReturn(true);

        ObjectMapper jsonMapper = new ObjectMapper();
        String amenityJson = jsonMapper.writeValueAsString(amenity);

        var request = put("/api/amenity/admin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(amenityJson);

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void updateShouldReturn400WhenInvalid() throws Exception {
        Amenity amenity = new Amenity(1, null);

        ObjectMapper jsonMapper = new ObjectMapper();
        String amenityJson = jsonMapper.writeValueAsString(amenity);

        var request = put("/api/amenity/admin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(amenityJson);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateShouldReturn409WhenIdsDoNotMatch() throws Exception {
        Amenity amenity = new Amenity(1, "Updated Amenity");

        ObjectMapper jsonMapper = new ObjectMapper();
        String amenityJson = jsonMapper.writeValueAsString(amenity);

        var request = put("/api/amenity/admin/9")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(amenityJson);

        mvc.perform(request)
                .andExpect(status().isConflict());
    }


    @Test
    void deleteShouldReturn204NoContent() throws Exception {


        when(amenityRepository.deleteById(1)).thenReturn(true);


        var request = delete("/api/amenity/admin/1")
                .header("Authorization", "Bearer " + token);

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturn404NotFoundWhenMissing() throws Exception {
        when(amenityRepository.deleteById(1)).thenReturn(false);

        var request = delete("/api/amenity/admin/1")
                .header("Authorization", "Bearer " + token);

        // Assert: Expecting 404 Not found as response
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }
}
