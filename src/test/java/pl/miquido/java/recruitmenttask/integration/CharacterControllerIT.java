package pl.miquido.java.recruitmenttask.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pl.miquido.java.recruitmenttask.BaseIntegrationTests;
import pl.miquido.java.recruitmenttask.entity.CharacterEntity;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CharacterControllerIT extends BaseIntegrationTests {
    @Autowired
    private MockMvc mvc;

    public static MockWebServer mockBackEnd;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @DynamicPropertySource
    static void backendProperties(DynamicPropertyRegistry registry) {
        registry.add("sw.api.base-url", () -> mockBackEnd.url("/").toString());
    }

    @Test
    public void loadCharacterIntoDBTestCorrect() throws Exception {
        CharacterEntity mockCharacter = new CharacterEntity(null, "Luke Skywalker", "77", "172");
        enqueueCorrect(mockCharacter);

        mvc.perform(get("/starwars/load/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Luke Skywalker"))
                .andExpect(jsonPath("$.mass").value("77"))
                .andExpect(jsonPath("$.height").value("172"));
    }

    @Test
    public void loadCharacterIntoDBTestIncorrectFormat() throws Exception {
        mvc.perform(get("/starwars/load/abcd"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("NumberFormatException"));
    }

    @Test
    public void loadCharacterIntoDBTestResourceNotFound() throws Exception {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(404));

        mvc.perform(get("/starwars/load/1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("IllegalArgumentException"))
                .andExpect(jsonPath("$.message")
                        .value("No resource found. Please check the Id."));
    }

    @Test
    public void loadCharacterIntoDBTestCharacterAlreadyExists() throws Exception {
        CharacterEntity mockCharacter = new CharacterEntity(null, "Darth vader", "136", "202");
        enqueueCorrect(mockCharacter);

        mvc.perform(get("/starwars/load/4"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("InappropriateCharacterException"))
                .andExpect(jsonPath("$.message")
                        .value("This character is inappropriate because: the character already exists with Id: 2748d8f9-4ea8-4d15-a57b-999b23e340f7"));
    }

    @Test
    public void loadCharacterIntoDBTestCharacterIsTooShort() throws Exception {
        CharacterEntity mockCharacter = new CharacterEntity(null, "R2-D2", "32", "96");
        enqueueCorrect(mockCharacter);

        mvc.perform(get("/starwars/load/3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("InappropriateCharacterException"))
                .andExpect(jsonPath("$.message")
                        .value("This character is inappropriate because: the character is shorter than the specified minimum of 160"));
    }

    @Test
    public void retrieveCharacterByIdTestCorrect() throws Exception {
        String id = "2748d8f9-4ea8-4d15-a57b-999b23e340f7";

        mvc.perform(get("/starwars/retrieve/id/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Darth Vader"))
                .andExpect(jsonPath("$.mass").value("136"))
                .andExpect(jsonPath("$.height").value("202"));
    }

    @Test
    public void retrieveCharacterByIdTestCharacterNotInDB() throws Exception {
        String randomId = UUID.randomUUID().toString();

        mvc.perform(get("/starwars/retrieve/id/{id}", randomId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("IllegalArgumentException"))
                .andExpect(jsonPath("$.message")
                        .value("No character found with id: " + randomId));
    }

    @Test
    public void retrieveCharacterByNameTestCorrectExactFullName() throws Exception {
        mvc.perform(get("/starwars/retrieve/name/{name}", "Darth Vader"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Darth Vader"))
                .andExpect(jsonPath("$.mass").value("136"))
                .andExpect(jsonPath("$.height").value("202"));
    }

    @Test
    public void retrieveCharacterByNameTestCorrectExactFullNameLowercase() throws Exception {
        mvc.perform(get("/starwars/retrieve/name/{name}", "darth vader"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Darth Vader"))
                .andExpect(jsonPath("$.mass").value("136"))
                .andExpect(jsonPath("$.height").value("202"));
    }

    @Test
    public void retrieveCharacterByNameTestCorrectExactIncompleteName() throws Exception {
        mvc.perform(get("/starwars/retrieve/name/{name}", "vader"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Darth Vader"))
                .andExpect(jsonPath("$.mass").value("136"))
                .andExpect(jsonPath("$.height").value("202"));
    }

    @Test
    public void retrieveCharacterByNameTestCorrectMisspelledCompleteName() throws Exception {
        mvc.perform(get("/starwars/retrieve/name/{name}", "dart vadre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Darth Vader"))
                .andExpect(jsonPath("$.mass").value("136"))
                .andExpect(jsonPath("$.height").value("202"));
    }

    @Test
    public void retrieveCharacterByNameTestCharacterNotInDB() throws Exception {
        String name = "R2-D2";

        mvc.perform(get("/starwars/retrieve/name/{name}", name))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("IllegalArgumentException"))
                .andExpect(jsonPath("$.message")
                        .value("No character found with name: " + name));
    }

    private void enqueueCorrect(CharacterEntity character) throws JsonProcessingException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(character))
                .addHeader("Content-Type", "application/json"));
    }
}
