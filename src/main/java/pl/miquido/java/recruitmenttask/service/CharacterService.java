package pl.miquido.java.recruitmenttask.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.miquido.java.recruitmenttask.entity.CharacterEntity;
import pl.miquido.java.recruitmenttask.exception.InappropriateCharacterException;
import pl.miquido.java.recruitmenttask.model.CharacterDTO;
import pl.miquido.java.recruitmenttask.repository.CharacterRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CharacterService {
    private final CharacterRepository repository;
    private final ModelMapper mapper;
    private final WebClient webClient;

    @Value("${sw.character.height.minimum:150}")
    private int minCharacterHeight;

    public CharacterDTO addCharacter(int id) {
        CharacterEntity newCharacter = getCharacterFromAPI(id);
        validateHeight(newCharacter);
        validateUniqueness(newCharacter);
        newCharacter.setId(UUID.randomUUID().toString());
        return mapper.map(repository.save(newCharacter), CharacterDTO.class);
    }

    public CharacterDTO retrieveCharacterByUUID(String uuid) {
        Optional<CharacterEntity> optionalCharacter = repository.findById(uuid);
        if (optionalCharacter.isEmpty()) {
            throw new IllegalArgumentException("No character found with id: " + uuid);
        }
        return mapper.map(optionalCharacter.get(), CharacterDTO.class);
    }

    public CharacterDTO retrieveCharacterByName(String name) {
        Optional<CharacterEntity> optionalCharacter = repository.findByNameFuzzy(name);
        if (optionalCharacter.isEmpty()) {
            throw new IllegalArgumentException("No character found with name: " + name);
        }
        return mapper.map(optionalCharacter.get(), CharacterDTO.class);
    }

    private CharacterEntity getCharacterFromAPI(int id) {
        return webClient.get()
                .uri(Integer.toString(id))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        response -> Mono.error(new IllegalArgumentException("No resource found. Please check the Id.")))
                .bodyToMono(CharacterEntity.class)
                .block();
    }

    private void validateHeight(CharacterEntity character) {
        double height = Double.parseDouble(character.getHeight());
        if (Double.isNaN(height) || height < minCharacterHeight) {
            throw new InappropriateCharacterException("the character is shorter than the specified minimum of " + minCharacterHeight);
        }
    }

    private void validateUniqueness(CharacterEntity character) {
        Optional<CharacterEntity> preexistingCharacter = repository.findByNameFuzzy(character.getName());
        if (preexistingCharacter.isPresent()) {
            throw new InappropriateCharacterException("the character already exists with Id: " + preexistingCharacter.get().getId());
        }
    }
}
