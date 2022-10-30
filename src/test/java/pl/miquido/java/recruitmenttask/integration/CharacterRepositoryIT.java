package pl.miquido.java.recruitmenttask.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.miquido.java.recruitmenttask.BaseIntegrationTests;
import pl.miquido.java.recruitmenttask.entity.CharacterEntity;
import pl.miquido.java.recruitmenttask.repository.CharacterRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CharacterRepositoryIT extends BaseIntegrationTests {
    @Autowired
    private CharacterRepository characterRepository;

    private final String id = "2748d8f9-4ea8-4d15-a57b-999b23e340f7";

    @Test
    public void findByIdTestCorrect() {
        Optional<CharacterEntity> optionalActual = characterRepository.findById(id);

        assertTrue(optionalActual.isPresent());

        CharacterEntity actual = optionalActual.get();

        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo("Darth Vader");
        assertThat(actual.getMass()).isEqualTo("136");
        assertThat(actual.getHeight()).isEqualTo("202");
    }

    @Test
    public void findByIdTestIncorrectUUID() {
        Optional<CharacterEntity> optionalActual = characterRepository.findById(UUID.randomUUID().toString());

        assertTrue(optionalActual.isEmpty());
    }

    @Test
    public void findByNameFuzzyTestExactName() {
        Optional<CharacterEntity> optionalActual = characterRepository.findByNameFuzzy("Darth Vader");

        assertTrue(optionalActual.isPresent());

        CharacterEntity actual = optionalActual.get();

        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo("Darth Vader");
        assertThat(actual.getMass()).isEqualTo("136");
        assertThat(actual.getHeight()).isEqualTo("202");
    }

    @Test
    public void findByNameFuzzyTestLowerCaseName() {
        Optional<CharacterEntity> optionalActual = characterRepository.findByNameFuzzy("darth vader");

        assertTrue(optionalActual.isPresent());

        CharacterEntity actual = optionalActual.get();

        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo("Darth Vader");
        assertThat(actual.getMass()).isEqualTo("136");
        assertThat(actual.getHeight()).isEqualTo("202");
    }

    @Test
    public void findByNameFuzzyTestMisspelledName() {
        Optional<CharacterEntity> optionalActual = characterRepository.findByNameFuzzy("dart vadre");

        assertTrue(optionalActual.isPresent());

        CharacterEntity actual = optionalActual.get();

        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo("Darth Vader");
        assertThat(actual.getMass()).isEqualTo("136");
        assertThat(actual.getHeight()).isEqualTo("202");
    }

    @Test
    public void findByNameFuzzyTestIncompleteButCorrectName() {
        Optional<CharacterEntity> optionalActual = characterRepository.findByNameFuzzy("vader");

        assertTrue(optionalActual.isPresent());

        CharacterEntity actual = optionalActual.get();

        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo("Darth Vader");
        assertThat(actual.getMass()).isEqualTo("136");
        assertThat(actual.getHeight()).isEqualTo("202");
    }
}
