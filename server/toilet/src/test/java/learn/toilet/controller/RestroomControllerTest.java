package learn.toilet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import learn.toilet.data.AppUserRepository;
import learn.toilet.data.RestroomRepository;
import learn.toilet.models.AppUser;
import learn.toilet.models.Restroom;
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
public class RestroomControllerTest {

    @MockBean
    RestroomRepository repository;

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

        var request = post("/api/restroom")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addShouldReturn400WhenInvalid() throws Exception {

        var request = post("/api/restroom")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addShouldReturn415WhenMultipart() throws Exception {

        ObjectMapper jsonMapper = new ObjectMapper();

        Restroom restroom = new Restroom(0, "bathroom", 40.748817, -73.985428, "10 apple street", "down the hall", "disgusting", 1);
        String restroomJson = jsonMapper.writeValueAsString(restroom);

        var request = post("/api/restroom")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token)
                .content(restroomJson);

        mvc.perform(request)
                .andExpect(status().isUnsupportedMediaType());
    }


    @Test
    void addShouldReturn201() throws Exception {

        Restroom restroom = new Restroom(0, "bathroom", 40.748817, -73.985428, "10 apple street", "down the hall", "disgusting", 1);
        Restroom expected = new Restroom(1, "bathroom", 40.748817, -73.985428, "10 apple street", "down the hall", "disgusting", 1);
        AppUser user = new AppUser(1, "1234", "$2a$10$QO8UzE8TDb1N6BQDwMTPGeV6HMYhgeffflkj4vwZ0jxDrhplKP8Yq", true, List.of("admin"));

        when(repository.add(any())).thenReturn(expected);
        when(appUserRepository.findById(1)).thenReturn(user);

        String restroomJson = jsonMapper.writeValueAsString(restroom);
        String expectedJson = jsonMapper.writeValueAsString(expected);

        var request = post("/api/restroom")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(restroomJson);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void updateShouldReturn204NoContent() throws Exception {
        Restroom restroom = new Restroom(1, "UPDATE", 40.748817, -73.985428, "10 apple street", "down the hall", "disgusting", 1);
        AppUser user = new AppUser(1, "1234", "$2a$10$QO8UzE8TDb1N6BQDwMTPGeV6HMYhgeffflkj4vwZ0jxDrhplKP8Yq", true, List.of("admin"));

        when(repository.update(any())).thenReturn(true);
        when(appUserRepository.findById(1)).thenReturn(user);

        String restroomJson = jsonMapper.writeValueAsString(restroom);

        var request = put("/api/restroom/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(restroomJson);

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void updateShouldReturn400WhenInvalid() throws Exception {
        Restroom restroom = new Restroom(1, null, 40.748817, -73.985428, "10 apple street", "down the hall", "disgusting", 1);

        String restroomJson = jsonMapper.writeValueAsString(restroom);

        var request = put("/api/restroom/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(restroomJson);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateShouldReturn409WhenIdsDoNotMatch() throws Exception {
        Restroom restroom = new Restroom(1, "UPDATE", 40.748817, -73.985428, "10 apple street", "down the hall", "disgusting", 1);

        String restroomJson = jsonMapper.writeValueAsString(restroom);

        var request = put("/api/restroom/9")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(restroomJson);

        mvc.perform(request)
                .andExpect(status().isConflict());
    }


    @Test
    void deleteShouldReturn204NoContent() throws Exception {

        when(repository.deleteById(1)).thenReturn(true);

        var request = delete("/api/restroom/1")
                .header("Authorization", "Bearer " + token);

        // Assert: Expecting 204 No Content as response
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturn404NotFoundWhenMissing() throws Exception {
        when(repository.deleteById(1)).thenReturn(false);

        var request = delete("/api/restroom/1")
                .header("Authorization", "Bearer " + token);

        // Assert: Expecting 404 Not found as response
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

}
