package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.exception.ErrorMessage.BREED_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.exception.ErrorMessage;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.repository.BreedRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class BreedServiceTest {

    @Mock
    private BreedRepository breedRepository;

    @InjectMocks
    private BreedService breedService;

    @Nested
    @DisplayName("이름을 통해 종을 찾는 메서드 테스트/findBreedByName")
    class 이름을_통해_종을_찾는_메서드 {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            String name = "testBreed";
            Breed expect = new Breed("testBreed");

            // when
            when(breedRepository.findByName(name))
                .thenReturn(Optional.of(expect));

            // then
            assertEquals(expect, breedService.findBreedByName(name));
        }

        @Test
        @DisplayName("해당 이름이 존재하지 않는 경우")
        void 해당_이름이_존재하지_않는_경우() {
            // given
            String name = "testBreed";

            // when
            when(breedRepository.findByName(name))
                .thenReturn(Optional.empty());

            // then
            CustomException thrown = assertThrows(
                CustomException.class,
                () -> breedService.findBreedByName(name)
            );

            assertAll(
                () -> assertEquals(BREED_NOT_FOUND, thrown.getErrorMessage()),
                () -> assertEquals(NOT_FOUND, thrown.getStatus())
            );
        }
    }
}