package pl.miquido.java.recruitmenttask.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.web.reactive.function.client.WebClient;
import pl.miquido.java.recruitmenttask.entity.CharacterEntity;
import pl.miquido.java.recruitmenttask.exception.InappropriateCharacterException;
import pl.miquido.java.recruitmenttask.model.CharacterDTO;
import pl.miquido.java.recruitmenttask.repository.CharacterRepository;
import pl.miquido.java.recruitmenttask.service.CharacterService;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CharacterServiceTests {
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private WebClient webClient;
    @Spy
    private ModelMapper modelMapper;
    @InjectMocks
    private CharacterService characterService;

    @Test
    public void addCharacterTestCorrect() {
        CharacterEntity mockCharacter = new CharacterEntity(null, "Luke Skywalker", "77", "172");
        mockWebClientCorrect(mockCharacter, 1);
        when(characterRepository.save(any(CharacterEntity.class))).then(returnsFirstArg());
        when(characterRepository.findByNameFuzzy(any(String.class))).thenReturn(Optional.empty());

        CharacterDTO actual = characterService.addCharacter(1);

        assertCorrectCase(actual, mockCharacter);
    }

    @Test
    public void addCharacterTestCharacterAlreadyExists() {
        CharacterEntity mockCharacter = new CharacterEntity(null, "Luke Skywalker", "77", "172");
        mockWebClientCorrect(mockCharacter, 1);
        when(characterRepository.save(any(CharacterEntity.class))).then(returnsFirstArg());
        when(characterRepository.findByNameFuzzy(any(String.class))).thenReturn(Optional.of(mockCharacter));

        String expectedMessage = "the character already exists";
        Exception exception = assertThrows(InappropriateCharacterException.class, () -> characterService.addCharacter(1));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void addCharacterTestCharacterIsTooShort() {
        org.springframework.test.util.ReflectionTestUtils.setField(characterService, "minCharacterHeight", 170);

        String expectedMessage = "the character is shorter than the specified minimum";

        CharacterEntity mockCharacter = new CharacterEntity(null, "C-3PO", "75", "167");
        mockWebClientCorrect(mockCharacter, 2);
        when(characterRepository.save(any(CharacterEntity.class))).then(returnsFirstArg());
        when(characterRepository.findByNameFuzzy(any(String.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(InappropriateCharacterException.class, () -> characterService.addCharacter(2));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void addCharacterTestResourceNotFound() {
        var uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        var headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var responseSpecMock = mock(WebClient.ResponseSpec.class);

        String expectedMessage = "No resource found. Please check the Id.";

        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri("1000")).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenThrow(new IllegalArgumentException(expectedMessage));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> characterService.addCharacter(1000));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void retrieveCharacterByUUIDTestCorrect() {
        CharacterEntity mockCharacter = new CharacterEntity(UUID.randomUUID().toString(), "Luke Skywalker", "77", "172");
        when(characterRepository.findById(any(String.class))).thenReturn(Optional.of(mockCharacter));

        CharacterDTO actual = characterService.retrieveCharacterByUUID(UUID.randomUUID().toString());

        assertCorrectCase(actual, mockCharacter);
    }

    @Test
    public void retrieveCharacterByUUIDTestNoCharacterFound() {
        when(characterRepository.findById(any(String.class))).thenReturn(Optional.empty());

        String expectedMessage = "No character found with id";

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> characterService.retrieveCharacterByUUID(UUID.randomUUID().toString()));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void retrieveCharacterByNameTestCorrect() {
        CharacterEntity mockCharacter = new CharacterEntity(UUID.randomUUID().toString(), "Luke Skywalker", "77", "172");
        when(characterRepository.findByNameFuzzy(mockCharacter.getName())).thenReturn(Optional.of(mockCharacter));

        CharacterDTO actual = characterService.retrieveCharacterByName("Luke Skywalker");

        assertCorrectCase(actual, mockCharacter);
    }

    @Test
    public void retrieveCharacterByNameTestNoCharacterFound() {
        when(characterRepository.findByNameFuzzy(any(String.class))).thenReturn(Optional.empty());

        String expectedMessage = "No character found with name";

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> characterService.retrieveCharacterByName(""));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    private void mockWebClientCorrect(final CharacterEntity character, int id) {
        var uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        var headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(Integer.toString(id))).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(CharacterEntity.class))
                .thenReturn(Mono.just(character));
    }

    private void assertCorrectCase(CharacterDTO actual, CharacterEntity expected) {
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getMass()).isEqualTo(expected.getMass());
        assertThat(actual.getHeight()).isEqualTo(expected.getHeight());
    }
}
