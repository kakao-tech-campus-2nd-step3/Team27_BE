package com.ktc.togetherPet.model.entity;

import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_PET_MONTH;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.ktc.togetherPet.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PetTest {

    @Nested
    @DisplayName("애완동물의 개월수가 0이상인지 테스트")
    class 애완동물의_개월수 {

        @ParameterizedTest
        @ValueSource(longs = {1, 2, 3, 4, 5, Long.MAX_VALUE})
        @DisplayName("개월수가 0초과로 정상적인 경우")
        void 개월수가_0초과(long birthMonth) {
            // given
            String name = "testPetName";
            Breed breed = new Breed("testBreed");
            boolean isNeutering = true;

            // when & then
            assertDoesNotThrow(
                () -> new Pet(name, birthMonth, breed, isNeutering)
            );
        }

        @ParameterizedTest
        @ValueSource(longs = {Long.MIN_VALUE, -999, 0})
        @DisplayName("개월수가 0이하로 잘못 입력된 경우")
        void 개월수가_0이하(long birthMonth) {
            // given
            String name = "testPetName";
            Breed breed = new Breed("testBreed");
            boolean isNeutering = true;

            // when & then
            CustomException thrown = assertThrows(
                CustomException.class,
                () -> new Pet(name, birthMonth, breed, isNeutering)
            );

            assertAll(
                () -> assertEquals(INVALID_PET_MONTH, thrown.getErrorMessage()),
                () -> assertEquals(BAD_REQUEST, thrown.getStatus())
            );
        }
    }
}