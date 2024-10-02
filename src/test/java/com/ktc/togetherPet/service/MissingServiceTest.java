package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.exception.CustomException.breedNotFoundException;
import static com.ktc.togetherPet.exception.CustomException.invalidUserException;
import static com.ktc.togetherPet.exception.ErrorMessage.BREED_NOT_FOUND;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_DATE;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_LOCATION;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_PET_MONTH;
import static com.ktc.togetherPet.exception.ErrorMessage.INVALID_USER;
import static com.ktc.togetherPet.exception.ErrorMessage.MISSING_NOT_FOUND;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.missing.MissingPetDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetDetailDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetNearByDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.BirthMonth;
import com.ktc.togetherPet.model.vo.DateTime;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.BreedRepository;
import com.ktc.togetherPet.repository.MissingRepository;
import com.ktc.togetherPet.repository.PetRepository;
import com.ktc.togetherPet.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

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

    @Mock
    private KakaoMapService kakaoMapService;

    @InjectMocks
    private MissingService missingService;

    @Nested
    @DisplayName("[같이 찾기] registerMissingPet 테스트")
    class registerMissingPetTest {

        private OauthUserDTO oauthUserDTO;
        private MissingPetDTO missingPetDTO;
        private Missing missing;
        private Pet pet;
        private Breed breed;
        private Location location;
        private long regionCode;

        @BeforeEach
        void setUp() {
            //given
            oauthUserDTO = new OauthUserDTO("test@email.com");

            missingPetDTO = new MissingPetDTO(
                "testPetName",
                "testPetGender",
                1L,
                "testPetSpecies",
                LocalDateTime.of(2024, 9, 25, 13, 19, 33),
                35.17F,
                126.90F,
                "testPetFeature",
                true
            );

            breed = new Breed();

            pet = new Pet(
                missingPetDTO.petName(),
                new BirthMonth(
                    missingPetDTO.birthMonth()
                ),
                breed,
                missingPetDTO.isNeutering()
            );

            location = new Location(
                missingPetDTO.latitude(),
                missingPetDTO.longitude()
            );

            regionCode = 1;

            missing = new Missing(
                pet,
                true,
                new DateTime(missingPetDTO.lostTime()),
                location,
                regionCode,
                missingPetDTO.description()
            );
        }

        @Nested
        @DisplayName("registerMissingPet 201 Created")
        class registerMissingPet_201 {

            @Test
            @DisplayName("펫이 존재하는 경우")
            void existPet() {
                //given
                User existPetUser = new User(oauthUserDTO.email());
                existPetUser.setPet(pet);

                //when
                given(userRepository.findByEmail(oauthUserDTO.email()))
                    .willReturn(Optional.of(existPetUser));

                given(kakaoMapService.getRegionCodeFromKakao(location))
                    .willReturn(regionCode);

                given(missingRepository.save(missing))
                    .willReturn(missing);

                //then
                missingService.registerMissingPet(oauthUserDTO, missingPetDTO);

                verify(userRepository, times(1))
                    .findByEmail(oauthUserDTO.email());

                verify(breedRepository, never()).findByName(missingPetDTO.petName());

                verify(petRepository, never()).save(pet);

                verify(missingRepository, times(1)).save(missing);
            }

            @Test
            @DisplayName("펫이 존재하지 않는 경우")
            void notExistPet() {
                //given
                User notExistsPetUser = new User(oauthUserDTO.email());

                //when
                given(userRepository.findByEmail(oauthUserDTO.email()))
                    .willReturn(Optional.of(notExistsPetUser));

                given(missingRepository.save(missing))
                    .willReturn(missing);

                given(petRepository.save(pet))
                    .willReturn(pet);

                given(breedRepository.findByName(missingPetDTO.petBreed()))
                    .willReturn(Optional.ofNullable(breed));

                given(kakaoMapService.getRegionCodeFromKakao(location))
                    .willReturn(regionCode);
                //then
                missingService.registerMissingPet(oauthUserDTO, missingPetDTO);

                verify(userRepository, times(1))
                    .findByEmail(oauthUserDTO.email());

                verify(breedRepository, times(1))
                    .findByName(missingPetDTO.petBreed());

                verify(petRepository, times(1))
                    .save(pet);

                verify(missingRepository, times(1))
                    .save(missing);
            }
        }

        @Nested
        @DisplayName("registerMissingPet 400 Bad Request")
        class registerMissingPet_400 {

            @ParameterizedTest
            @DisplayName("개월수가 0이하인 값이 입력된 경우")
            @ValueSource(ints = {-100, -1, 0})
            void underAgeZero(int petBirthMonth) {
                //given
                User user = new User(oauthUserDTO.email());

                missingPetDTO = new MissingPetDTO(
                    "testPetName",
                    "testPetGender",
                    petBirthMonth,
                    "testPetSpecies",
                    LocalDateTime.of(2024, 9, 25, 13, 19, 33),
                    35.17F,
                    126.90F,
                    "testPetFeature",
                    true
                );

                //when
                given(userRepository.findByEmail(oauthUserDTO.email()))
                    .willReturn(Optional.of(user));

                //then
                CustomException thrown = assertThrows(
                    CustomException.class,
                    () -> missingService.registerMissingPet(oauthUserDTO, missingPetDTO)
                );

                assertAll(
                    () -> assertEquals(thrown.getStatus(), BAD_REQUEST),
                    () -> assertEquals(thrown.getMessage(), INVALID_PET_MONTH)
                );
            }

            @ParameterizedTest
            @DisplayName("시간이 현재보다 늦는 경우")
            @MethodSource("lateMoreThanNowMethodSource")
            void lateMoreThanNow(LocalDateTime lostTime) {
                //given
                User user = new User(oauthUserDTO.email());

                missingPetDTO = new MissingPetDTO(
                    "testPetName",
                    "testPetGender",
                    1L,
                    "testPetSpecies",
                    lostTime,
                    35.17F,
                    126.90F,
                    "testPetFeature",
                    true
                );

                //when
                given(userRepository.findByEmail(oauthUserDTO.email()))
                    .willReturn(Optional.of(user));

                given(breedRepository.findByName(missingPetDTO.petBreed()))
                    .willReturn(Optional.ofNullable(breed));

                //then
                CustomException thrown = assertThrows(
                    CustomException.class,
                    () -> missingService.registerMissingPet(oauthUserDTO, missingPetDTO)
                );

                assertAll(
                    () -> assertEquals(thrown.getStatus(), BAD_REQUEST),
                    () -> assertEquals(thrown.getMessage(), INVALID_DATE)
                );
            }

            private static Stream<LocalDateTime> lateMoreThanNowMethodSource() {
                return Stream.of(
                    LocalDateTime.now().plusHours(1L).truncatedTo(SECONDS),
                    LocalDateTime.now().plusMinutes(10L).truncatedTo(SECONDS)
                );
            }

            @ParameterizedTest
            @DisplayName("위도와 경도가 잘못된 값으로 입력된 경우")
            @MethodSource("incorrectRangeOfLatitudeAndLongitudeMethodSource")
            void incorrectRangeOfLatitudeAndLongitude(float lostLatitude, float lostLongitude) {
                // given
                User user = new User(oauthUserDTO.email());

                missingPetDTO = new MissingPetDTO(
                    "testPetName",
                    "testPetGender",
                    1L,
                    "testPetSpecies",
                    LocalDateTime.of(2024, 9, 25, 13, 19, 33),
                    lostLatitude,
                    lostLongitude,
                    "testPetFeature",
                    true
                );

                //when
                given(userRepository.findByEmail(oauthUserDTO.email()))
                    .willReturn(Optional.of(user));

                given(breedRepository.findByName(missingPetDTO.petBreed()))
                    .willReturn(Optional.of(breed));

                //then
                CustomException thrown = assertThrows(
                    CustomException.class,
                    () -> missingService.registerMissingPet(oauthUserDTO, missingPetDTO)
                );

                assertAll(
                    () -> assertEquals(thrown.getStatus(), BAD_REQUEST),
                    () -> assertEquals(thrown.getMessage(), INVALID_LOCATION)
                );

            }

            private static Stream<Arguments> incorrectRangeOfLatitudeAndLongitudeMethodSource() {
                return Stream.of(
                    Arguments.of(-91, 0),
                    Arguments.of(91, 0),
                    Arguments.of(0, -181),
                    Arguments.of(0, 181),
                    Arguments.of(-91, 181),
                    Arguments.of(-90.00001F, 0)
                );
            }
        }

        @Test
        @DisplayName("유효하지 않는 사용자의 경우")
        void invalidUser() {
            //when
            given(userRepository.findByEmail(oauthUserDTO.email()))
                .willThrow(invalidUserException());

            //then
            CustomException thrown = assertThrows(
                CustomException.class,
                () -> missingService.registerMissingPet(oauthUserDTO, missingPetDTO)
            );

            assertAll(
                () -> assertEquals(thrown.getStatus(), UNAUTHORIZED),
                () -> assertEquals(thrown.getMessage(), INVALID_USER)
            );
        }

        @Test
        @DisplayName("유효하지 않은 종의 경우")
        void invalidBreed() {
            //given
            User user = new User(oauthUserDTO.email());

            //when
            given(userRepository.findByEmail(oauthUserDTO.email()))
                .willReturn(Optional.of(user));

            given(breedRepository.findByName(missingPetDTO.petBreed()))
                .willThrow(breedNotFoundException());

            //then
            CustomException thrown = assertThrows(
                CustomException.class,
                () -> missingService.registerMissingPet(oauthUserDTO, missingPetDTO)
            );

            assertAll(
                () -> assertEquals(thrown.getStatus(), NOT_FOUND),
                () -> assertEquals(thrown.getMessage(), BREED_NOT_FOUND)
            );
        }
    }

    @Nested
    @DisplayName("[같이 찾기] getMissingPetsNearBy 테스트")
    class getMissingPetsNearByTest {

        @Test
        @DisplayName("getMissingPetsNearBy_200")
        void getMissingPetsNearBy_200() {
            //given
            float latitude = 35.12F;
            float longitude = 126.90F;
            long regionCode = 1L;

            Pet pet1 = spy(new Pet(
                "test-name-1",
                new BirthMonth(1L),
                new Breed(),
                true
            ));

            Pet pet2 = spy(new Pet(
                "test-name-2",
                new BirthMonth(2L),
                new Breed(),
                false
            ));

            Pet pet3 = spy(new Pet(
                "test-name-3",
                new BirthMonth(3L),
                new Breed(),
                false
            ));

            List<Missing> missingList = List.of(
                new Missing(
                    pet1,
                    true,
                    new DateTime(LocalDateTime.now()),
                    new Location(32.5F, 80.0F),
                    regionCode,
                    "test-description-1"
                ),
                new Missing(
                    pet2,
                    true,
                    new DateTime(LocalDateTime.now().minus(1, HOURS)),
                    new Location(33.5F, 81.0F),
                    regionCode,
                    "test-description-2"
                ),
                new Missing(
                    pet3,
                    false,
                    new DateTime(LocalDateTime.now().minus(1, MINUTES)),
                    new Location(33.0F, 126.0F),
                    regionCode,
                    "test-description-3"
                )
            );

            List<MissingPetNearByDTO> expects = List.of(
                new MissingPetNearByDTO(
                    1L,
                    32.5F,
                    80.0F,
                    "pet-image-url"
                ),
                new MissingPetNearByDTO(
                    2L,
                    33.5F,
                    81.0F,
                    "pet-image-url"
                )
            );

            //when
            when(kakaoMapService.getRegionCodeFromKakao(new Location(latitude, longitude)))
                .thenReturn(regionCode);

            when(missingRepository.findAllByRegionCode(regionCode))
                .thenReturn(missingList);

            when(pet1.getId()).thenReturn(1L);
            when(pet2.getId()).thenReturn(2L);

            //then
            List<MissingPetNearByDTO> result = missingService.getMissingPetsNearBy(latitude,
                longitude);
            assertEquals(expects, result);
        }
    }

    @Nested
    @DisplayName("[같이 찾기] getMissingPetDetailById 테스트")
    class getMissingPetDetatilByIdTest {

        private final long missingId = 1L;

        @Test
        @DisplayName("getMissingPetDetailById_200")
        void getMissingPetDetailById_200() {
            // given
            Missing expectMissing = new Missing(
                new Pet(
                    "test-pet-name",
                    new BirthMonth(1L),
                    new Breed("test-breed-name"),
                    true
                ),
                true,
                new DateTime(LocalDateTime.now()),
                new Location(10.0F, 15.0F),
                15,
                "test-description-1"
            );
            MissingPetDetailDTO expect = new MissingPetDetailDTO(
                "test-pet-name",
                "test-breed-name",
                1L,
                10.0F,
                15.0F,
                "test-description-1",
                // TODO 실제 펫의 image-url로 변경하도록 수정 필요
                List.of("pet-image-url")
            );

            // when
            when(missingRepository.findById(missingId))
                .thenReturn(Optional.of(expectMissing));

            // then
            MissingPetDetailDTO result = missingService.getMissingPetDetailById(missingId);
            assertEquals(expect, result);

            verify(missingRepository, times(1))
                .findById(missingId);
        }

        @Test
        @DisplayName("getMissingPetDetailById_404")
        void getMissingPetDetailById_404() {
            // when
            when(missingRepository.findById(missingId))
                .thenThrow(CustomException.missingNotFound());

            // then
            CustomException thrown = assertThrows(
                CustomException.class,
                () -> missingService.getMissingPetDetailById(missingId)
            );

            assertAll(
                () -> assertEquals(thrown.getStatus(), NOT_FOUND),
                () -> assertEquals(thrown.getMessage(), MISSING_NOT_FOUND)
            );
        }
    }
}