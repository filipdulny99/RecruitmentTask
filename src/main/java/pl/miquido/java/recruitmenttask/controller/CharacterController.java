package pl.miquido.java.recruitmenttask.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.miquido.java.recruitmenttask.model.CharacterDTO;
import pl.miquido.java.recruitmenttask.service.CharacterService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/starwars")
public class CharacterController {
    private final CharacterService characterService;

    @ApiOperation(value = "Load a Star Wars character into the database",
            notes = "Input a positive integer to choose your character form the " +
                    "Star Wars API and save it to the database.")
    @GetMapping("/load/{id}")
    public ResponseEntity<CharacterDTO> loadCharacterIntoDB(@PathVariable int id) {
        return ResponseEntity.ok(characterService.addCharacter(id));
    }

    @ApiOperation(value = "Get a Star Wars character by their UUID",
            notes = "Input a UUID of the character you want to receive information on.")
    @GetMapping("/retrieve/id/{id}")
    public ResponseEntity<CharacterDTO> retrieveCharacterById(@PathVariable String id) {
        return ResponseEntity.ok(characterService.retrieveCharacterByUUID(id));
    }

    @ApiOperation(value = "Get a Star Wars character by their name",
            notes = "Input the name of the character you want to receive information on " +
                    "- full name or correct part of the name is required.")
    @GetMapping("/retrieve/name/{name}")
    public ResponseEntity<CharacterDTO> retrieveCharacterByName(@PathVariable String name) {
        return ResponseEntity.ok(characterService.retrieveCharacterByName(name));
    }
}
