package com.ktc.togetherPet.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ktc.togetherPet.model.entity.Breed;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class BreedRepositoryTest {

    @Autowired
    private BreedRepository breedRepository;

    @Nested
    @DisplayName("이름으로 종을 찾는 테스트/findByName")
    class 이름으로_종을_찾기 {

        @Test
        @DisplayName("해당 종이 존재하는 경우")
        void 해당_종이_존재하는_경우() {
            // given
            String breedName = "찾을려는 이름";
            Breed breed = new Breed(breedName);

            // when
            breedRepository.save(breed);
            breedRepository.save(new Breed("이건 다른 이름"));
            breedRepository.save(new Breed("이것도 다른 이름"));

            // then
            Optional<Breed> expect = breedRepository.findByName(breedName);

            assertAll(
                () -> assertTrue(expect.isPresent()),
                () -> assertEquals(breed, expect.get())
            );
        }

        @Test
        @DisplayName("해당 종이 존재하지 않는 경우")
        void 해당_종이_존재하지_않는_경우() {
            // given
            String breedName = "찾을려는 이름";

            // when & then
            breedRepository.save(new Breed("이건 다른 이름"));
            breedRepository.save(new Breed("이것도 다른 이름"));
            Optional<Breed> expect = breedRepository.findByName(breedName);

            assertTrue(expect.isEmpty());
        }
    }
}