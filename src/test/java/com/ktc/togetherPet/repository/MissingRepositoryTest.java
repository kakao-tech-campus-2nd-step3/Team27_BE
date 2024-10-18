package com.ktc.togetherPet.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.vo.Location;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    private List<Pet> givenPet;
    private List<Missing> givenMissing;

    @BeforeEach
    void setUp() {
        List<Breed> givenBreed = List.of(
            new Breed("testBreed1"),
            new Breed("testBreed2")
        );

        breedRepository.saveAll(givenBreed);

        givenPet = List.of(
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

        petRepository.saveAll(givenPet);

        givenMissing = List.of(
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
            )
        );
    }

    @Test
    @DisplayName("실종 코드를 통한 실종 정보 찾기/findAllByRegionCode")
    void 실종_코드를_통한_실종_정보_찾기() {
        // given
        List<Missing> expect = List.of(
            givenMissing.get(0),
            givenMissing.get(1)
        );

        // when
        missingRepository.saveAll(givenMissing);

        // then
        List<Missing> actual = missingRepository.findAllByRegionCode(1L);
        assertEquals(actual, expect);
    }

    @Nested
    @DisplayName("애완동물을 기반으로 실종 정보 찾기 테스트/findByPet")
    class 애완동물을_기반으로_실종_정보_찾기 {

        @Test
        @DisplayName("존재하는 경우")
        void 존재하는_경우() {
            // given
            Pet pet = givenPet.getFirst();
            Optional<Missing> expect = Optional.of(givenMissing.get(0));

            // when
            missingRepository.saveAll(givenMissing);

            // then
            Optional<Missing> actual = missingRepository.findByPet(pet);
            assertEquals(actual, expect);
        }

        @Test
        @DisplayName("존재하지 않는 경우")
        void 존재하지_않는_경우() {
            // given
            Pet pet = givenPet.getLast();

            // when
            missingRepository.saveAll(givenMissing);

            // then
            assertTrue(missingRepository.findByPet(pet).isEmpty());
        }
    }
}