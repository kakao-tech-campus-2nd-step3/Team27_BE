package com.ktc.togetherPet.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.vo.Location;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class MissingRepositoryTest {

    @Autowired
    private MissingRepository missingRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private BreedRepository breedRepository;

    @Test
    @DisplayName("실종 코드를 통한 실종 정보 찾기/findAllByRegionCode")
    void 실종_코드를_통한_실종_정보_찾기() {
        // given
        List<Breed> givenBreed = List.of(
            new Breed("testBreed1"),
            new Breed("testBreed2")
        );

        List<Pet> givenPet = List.of(
            new Pet(
                "testPetName1",
                1L,
                givenBreed.get(0),
                true
            ),
            new Pet(
                "testPetName2",
                2L,
                givenBreed.get(1),
                false
            ),
            new Pet(
                "testPetName3",
                2L,
                givenBreed.get(1),
                true
            ),
            new Pet(
                "testPetName4",
                12L,
                givenBreed.get(0),
                false
            )
        );

        List<Missing> givenMissing = List.of(
            new Missing(
                givenPet.get(0),
                true,
                LocalDateTime.of(2024, 10, 17, 12, 40, 44),
                new Location(15.0D, 15D),
                1L,
                "testDescription1"
            ),
            new Missing(
                givenPet.get(1),
                false,
                LocalDateTime.of(2024, 10, 17, 12, 40, 22),
                new Location(15.0D, 15D),
                1L,
                "testDescription2"
            ),
            new Missing(
                givenPet.get(2),
                false,
                LocalDateTime.of(2024, 10, 17, 12, 40, 22),
                new Location(20D, 20D),
                2L,
                "testDescription3"
            ),
            new Missing(
                givenPet.get(3),
                true,
                LocalDateTime.of(2024, 10, 17, 12, 40, 1),
                new Location(20D, 20D),
                2L,
                "testDescription4"
            )
        );

        List<Missing> expect = List.of(
            givenMissing.get(0),
            givenMissing.get(1)
        );

        // when
        breedRepository.saveAll(givenBreed);
        petRepository.saveAll(givenPet);
        missingRepository.saveAll(givenMissing);

        // then
        List<Missing> actual = missingRepository.findAllByRegionCode(1L);
        assertEquals(actual, expect);
    }
}