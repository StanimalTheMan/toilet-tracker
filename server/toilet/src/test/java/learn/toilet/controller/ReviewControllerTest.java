package learn.toilet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import learn.toilet.data.AppUserRepository;
import learn.toilet.data.ReviewRepository;
import learn.toilet.models.AppUser;
import learn.toilet.models.Restroom;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerTest {

    @MockBean
    ReviewRepository repository;

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

        var request = post("/api/review")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addShouldReturn400WhenInvalid() throws Exception {

        ObjectMapper jsonMapper = new ObjectMapper();

        Review review = new Review();
        review.setUserId(1);
        String reviewJson = jsonMapper.writeValueAsString(review);

        var request = post("/api/review")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(reviewJson);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addShouldReturn415WhenMultipart() throws Exception {

        ObjectMapper jsonMapper = new ObjectMapper();

        Review review = new Review(0, 1, "TEST", null, LocalDate.now(), 1, 1);
        String reviewJson = jsonMapper.writeValueAsString(review);

        var request = post("/api/review")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token)
                .content(reviewJson);

        mvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }


    @Test
    void addShouldReturn201() throws Exception {

        Timestamp t = new Timestamp(System.currentTimeMillis());
        Review review = new Review(0, 1, "TEST", t, LocalDate.now(), 1, 1);
        Review expected = new Review(1, 1, "TEST", t, LocalDate.now(), 1, 1);

        when(repository.add(any())).thenReturn(expected);

        String reviewJson = jsonMapper.writeValueAsString(review);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        var request = post("/api/review")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(reviewJson);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void updateShouldReturn204NoContent() throws Exception {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        Review review = new Review(1, 1, "UPDATED TEXT", t, LocalDate.now(), 1, 1);


        when(repository.update(any())).thenReturn(true);

        String reviewJson = jsonMapper.writeValueAsString(review);

        var request = put("/api/review/1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(reviewJson);

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void updateShouldReturn400WhenInvalid() throws Exception {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        Review review = new Review(1, 1, null, t, LocalDate.now(), 1, 1);

        String reviewJson = jsonMapper.writeValueAsString(review);

        var request = put("/api/review/1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(reviewJson);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateShouldReturn409WhenIdsDoNotMatch() throws Exception {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        Review review = new Review(1, 1, null, t, LocalDate.now(), 1, 1);

        String reviewJson = jsonMapper.writeValueAsString(review);

        var request = put("/api/review/1/9")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(reviewJson);

        mvc.perform(request)
                .andExpect(status().isConflict());
    }


    @Test
    void deleteShouldReturn204NoContent() throws Exception {


        when(repository.deleteById(1)).thenReturn(true);


        var request = delete("/api/review/1")
                .header("Authorization", "Bearer " + token);

        // Assert: Expecting 204 No Content as response
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturn404NotFoundWhenMissing() throws Exception {
        when(repository.deleteById(1)).thenReturn(false);

        var request = delete("/api/review/1")
                .header("Authorization", "Bearer " + token);

        // Assert: Expecting 404 Not found as response
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

}
