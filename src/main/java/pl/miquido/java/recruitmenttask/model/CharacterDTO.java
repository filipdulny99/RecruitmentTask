package pl.miquido.java.recruitmenttask.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Representation of a character")
public class CharacterDTO {
    @ApiModelProperty(notes = "A UUID tied to this character", example = "47953f69-3fb3-48a3-9509-19767882c007")
    private String id;
    @ApiModelProperty(notes = "Name of the character", example = "Darth Vader")
    private String name;
    @ApiModelProperty(notes = "Height of the character", example = "202")
    private String height;
    @ApiModelProperty(notes = "Weight of the character", example = "136")
    private String mass;
}
