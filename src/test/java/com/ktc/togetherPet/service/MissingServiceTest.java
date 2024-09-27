package com.ktc.togetherPet.service;

import com.ktc.togetherPet.repository.BreedRepository;
import com.ktc.togetherPet.repository.MissingRepository;
import com.ktc.togetherPet.repository.PetRepository;
import com.ktc.togetherPet.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MissingServiceTest {

    @Mock
    private MissingRepository missingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private BreedRepository breedRepository;

    @Nested
    @DisplayName("[같이 찾기] registerMissingPet 테스트")
    class registerMissingPetTest {

        @Nested
        @DisplayName("registerMissingPet 201 Created")
        class registerMissingPet_201 {

            @Test
            @DisplayName("펫이 존재하는 경우")
            void existPet() {
                // TODO 펫이 존재하는 경우에 대한 테스트 코드 작성
            }

            @Test
            @DisplayName("펫이 존재하지 않는 경우")
            void notExistPet() {
                // TODO 펫이 존재하지 않는 경우에 대한 테스트 코드 작성
            }
        }

        @Nested
        @DisplayName("registerMissingPet 400 Bad Request")
        class registerMissingPet_400 {

            @Test
            @DisplayName("개월수가 0이하인 값이 입력된 경우")
            void underAgeZero() {
                // TODO 개월수가 0이하인 값이 입력된 경우에 대한 테스트 코드 작성
            }

            @Test
            @DisplayName("시간이 현재보다 늦는 경우")
            void lateMoreThanNow() {
                // TODO 시간이 현재보다 늦는 경우에 대한 테스트 코드 작성
            }

            @Test
            @DisplayName("위도와 경도가 잘못된 값으로 입력된 경우")
            void incorrectRangeOfLatitudeAndLongitude() {
                // TODO 위도와 경도가 잘못된 값으로 입력된 경우에 대한 테스트 코드 작성
            }
        }

        @Test
        @DisplayName("유효하지 않는 사용자의 경우")
        void invalidUser() {
            // TODO 유효하지 않는 사용자에 대한 테스트 코드 작성
        }

        @Test
        @DisplayName("유효하지 않은 종의 경우")
        void invalidBreed() {
            // TODO 유효하지 않는 종에 대한 테스트 코드 작성
        }
    }
}