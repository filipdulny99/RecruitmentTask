package pl.miquido.java.recruitmenttask.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sw_character")
public class CharacterEntity {
    @Id
    @Column(name = "character_id")
    private String id;
    private String name;
    private String mass;
    private String height;
}
