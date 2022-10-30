package pl.miquido.java.recruitmenttask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.miquido.java.recruitmenttask.entity.CharacterEntity;

import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<CharacterEntity, Integer> {
    Optional<CharacterEntity> findById(String id);

    @Query(value = "SELECT * FROM sw_character WHERE name SOUNDS LIKE :nameFuzzy OR name LIKE concat('%', :nameFuzzy, '%')",
            nativeQuery = true)
    Optional<CharacterEntity> findByNameFuzzy(@Param("nameFuzzy") String nameFuzzy);
}
