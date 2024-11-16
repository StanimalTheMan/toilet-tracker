package learn.toilet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import learn.toilet.data.AppUserRepository;
import learn.toilet.data.RestroomAmenityRepository;
import learn.toilet.data.RestroomRepository;
import learn.toilet.models.Amenity;
import learn.toilet.models.AppUser;
import learn.toilet.models.Restroom;
import learn.toilet.models.RestroomAmenity;
import learn.toilet.security.JwtConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RestroomAmenityControllerTest {

    @MockBean
    RestroomAmenityRepository repository;

    @MockBean
    AppUserRepository appUserRepository;

    @Autowired
    MockMvc mvc;

    @Autowired
    JwtConverter jwtConverter;

    String token;

    private final ObjectMapper jsonMapper = new ObjectMapper();


    @BeforeEach
    void setup() {

        AppUser appUser = new AppUser(1, "dono", "2223", false,
                List.of("ADMIN"));

        when(appUserRepository.findByUsername("dono")).thenReturn(appUser);

        token = jwtConverter.getTokenFromUser(appUser);
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    }


    @Test
    void addShouldReturn400WhenEmpty() throws Exception {

        var request = post("/api/restroom/amenity")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addShouldReturn400WhenInvalid() throws Exception {
        var request = post("/api/restroom/amenity")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addShouldReturn415WhenMultipart() throws Exception {

        ObjectMapper jsonMapper = new ObjectMapper();

        RestroomAmenity restroomAmenity = new RestroomAmenity();
        String restroomAmenityJson = jsonMapper.writeValueAsString(restroomAmenity);

        var request = post("/api/restroom/amenity")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token)
                .content(restroomAmenityJson);

        mvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }


    @Test
    void addShouldReturn201() throws Exception {

        RestroomAmenity restroomAmenity = new RestroomAmenity();
        restroomAmenity.setRestroomId(1);
        restroomAmenity.setAmenity(new Amenity(1, "Test"));

        when(repository.add(any())).thenReturn(true);

        String restroomAmenityJson = jsonMapper.writeValueAsString(restroomAmenity);

        var request = post("/api/restroom/amenity")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(restroomAmenityJson);

        mvc.perform(request)
                .andExpect(status().isCreated());
    }

    @Test
    void deleteShouldReturn204NoContent() throws Exception {

        when(repository.deleteByKey(1, 1)).thenReturn(true);

        var request = delete("/api/restroom/amenity/1/1")
                .header("Authorization", "Bearer " + token);

        // Assert: Expecting 204 No Content as response
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturn404NotFoundWhenMissing() throws Exception {
        when(repository.deleteByKey(1, 1)).thenReturn(false);

        var request = delete("/api/restroom/amenity/1/1")
                .header("Authorization", "Bearer " + token);

        // Assert: Expecting 404 Not found as response
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }


}
