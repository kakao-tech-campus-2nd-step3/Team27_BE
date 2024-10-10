package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.MISSING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ktc.togetherPet.model.dto.missing.MissingPetNearByResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetRequestDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.MissingRepository;
import com.ktc.togetherPet.repository.PetRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MissingServiceTest {

    @Mock
    private MissingRepository missingRepository;

    @Mock
    private UserService userService;

    @Mock
    private PetRepository petRepository;

    @Mock
    private BreedService breedService;

    @Mock
    private KakaoMapService kakaoMapService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private MissingService missingService;

    @Nested
    @DisplayName("실종 등록 테스트/registerMissingPet")
    class registerMissingPet {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            OauthUserDTO oauthUserDTO = new OauthUserDTO("test@email.com");
            MissingPetRequestDTO missingPetRequestDTO = new MissingPetRequestDTO(
                "testPetName",
                "testPetGender",
                1L,
                "testPetBreed",
                LocalDateTime.of(2024, 10, 11, 3, 2, 22),
                15.0D,
                15.0D,
                "testDescription",
                true
            );

            User expectUser = new User(oauthUserDTO.email());

            Pet pet = new Pet(
                missingPetRequestDTO.petName(),
                missingPetRequestDTO.birthMonth(),
                new Breed(missingPetRequestDTO.petBreed()),
                missingPetRequestDTO.isNeutering()
            );

            Location location = new Location(
                missingPetRequestDTO.latitude(),
                missingPetRequestDTO.longitude()
            );

            expectUser.setPet(pet);

            long expectRegionCode = 1L;

            // when
            when(userService.findUserByEmail("test@email.com"))
                .thenReturn(expectUser);

            when(kakaoMapService.getRegionCodeFromKakao(location))
                .thenReturn(expectRegionCode);

            // then
            missingService.registerMissingPet(oauthUserDTO, missingPetRequestDTO);

            verify(userService, times(1))
                .findUserByEmail("test@email.com");

            verify(petRepository, never())
                .save(pet);

            verify(breedService, never())
                .findBreedByName(missingPetRequestDTO.petBreed());

            verify(missingRepository, times(1))
                .save(new Missing(
                    pet,
                    true,
                    missingPetRequestDTO.lostTime(),
                    location,
                    expectRegionCode,
                    missingPetRequestDTO.description()
                ));
        }
    }

    @Test
    @DisplayName("근처의 실종 동물 정보 가져오기 테스트/getMissingPetsNearBy")
    void 성공() {
        // given
        double latitude = 15.0D;
        double longitude = 15.0D;

        long expectRegionCode = 1L;

        Pet pet1 = spy(
            new Pet(
                "testPetName1",
                1L,
                new Breed("testBreed1"),
                true
            )
        );

        Pet pet2 = spy(
            new Pet(
                "testPetName2",
                2L,
                new Breed("testBreed2"),
                true
            )
        );

        Pet pet3 = new Pet(
            "testPetName3",
            3L,
            new Breed("testBreed1"),
            false
        );

        List<Missing> expectMissing = List.of(
            spy(new Missing(
                pet1,
                true,
                LocalDateTime.of(2024, 10, 11, 3, 49, 44),
                new Location(15.1D, 15.1D),
                expectRegionCode,
                "testDescription1"
            )),
            spy(new Missing(
                pet2,
                true,
                LocalDateTime.of(2024, 10, 1, 3, 49, 44),
                new Location(15.2D, 15.2D),
                expectRegionCode,
                "testDescription2"
            )),
            new Missing(
                pet3,
                false,
                LocalDateTime.of(2023, 10, 11, 3, 49, 44),
                new Location(15.0D, 15.0D),
                expectRegionCode,
                "testDescription3"
            )
        );

        String expectImageUrl1 = "https://together-pet/images/test-image-1.jpeg";
        String expectImageUrl2 = "https://together-pet/images/test-image-2.jpeg";

        List<MissingPetNearByResponseDTO> expect = List.of(
            new MissingPetNearByResponseDTO(
                1L,
                1L,
                15.1D,
                15.1D,
                expectImageUrl1
            ),
            new MissingPetNearByResponseDTO(
                2L,
                2L,
                15.2D,
                15.2D,
                expectImageUrl2
            )
        );

        // when
        when(kakaoMapService.getRegionCodeFromKakao(new Location(latitude, longitude)))
            .thenReturn(expectRegionCode);

        when(missingRepository.findAllByRegionCode(expectRegionCode))
            .thenReturn(expectMissing);

        when(pet1.getId())
            .thenReturn(1L);

        when(pet2.getId())
            .thenReturn(2L);

        when(expectMissing.get(0).getId())
            .thenReturn(1L);

        when(expectMissing.get(1).getId())
            .thenReturn(2L);

        when(imageService.getRepresentativeImageById(MISSING, expectMissing.get(0).getId()))
            .thenReturn(expectImageUrl1);

        when(imageService.getRepresentativeImageById(MISSING, expectMissing.get(1).getId()))
            .thenReturn(expectImageUrl2);

        // then
        List<MissingPetNearByResponseDTO> actual = missingService.getMissingPetsNearBy(latitude,
            longitude);

        assertEquals(actual, expect);

        verify(kakaoMapService, times(1))
            .getRegionCodeFromKakao(new Location(latitude, longitude));

        verify(missingRepository, times(1))
            .findAllByRegionCode(expectRegionCode);

        verify(imageService, times(1))
            .getRepresentativeImageById(MISSING, expectMissing.get(0).getId());

        verify(imageService, times(1))
            .getRepresentativeImageById(MISSING, expectMissing.get(1).getId());
    }

//
//    @Nested
//    @DisplayName("[같이 찾기] registerMissingPet 테스트")
//    class registerMissingPetTest {
//
//        private OauthUserDTO oauthUserDTO;
//        private MissingPetRequestDTO missingPetDTO;
//        private Missing missing;
//        private Pet pet;
//        private Breed breed;
//        private Location location;
//        private long regionCode;
//
//        @BeforeEach
//        void setUp() {
//            //given
//            oauthUserDTO = new OauthUserDTO("test@email.com");
//
//            missingPetDTO = new MissingPetRequestDTO(
//                "testPetName",
//                "testPetGender",
//                1L,
//                "testPetSpecies",
//                LocalDateTime.of(2024, 9, 25, 13, 19, 33),
//                35.17F,
//                126.90F,
//                "testPetFeature",
//                true
//            );
//
//            breed = new Breed();
//
//            pet = new Pet(
//                missingPetDTO.petName(),
//                missingPetDTO.birthMonth(),
//                breed,
//                missingPetDTO.isNeutering()
//            );
//
//            location = new Location(
//                missingPetDTO.latitude(),
//                missingPetDTO.longitude()
//            );
//
//            regionCode = 1;
//
//            missing = new Missing(
//                pet,
//                true,
//                missingPetDTO.lostTime(),
//                location,
//                regionCode,
//                missingPetDTO.description()
//            );
//        }
//
//        @Nested
//        @DisplayName("registerMissingPet 201 Created")
//        class registerMissingPet_201 {
//
//            @Test
//            @DisplayName("펫이 존재하는 경우")
//            void existPet() {
//                //given
//                User existPetUser = new User(oauthUserDTO.email());
//                existPetUser.setPet(pet);
//
//                //when
//                given(userRepository.findByEmail(oauthUserDTO.email()))
//                    .willReturn(Optional.of(existPetUser));
//
//                given(kakaoMapService.getRegionCodeFromKakao(location))
//                    .willReturn(regionCode);
//
//                given(missingRepository.save(missing))
//                    .willReturn(missing);
//
//                //then
//                missingService.registerMissingPet(oauthUserDTO, missingPetDTO);
//
//                verify(userRepository, times(1))
//                    .findByEmail(oauthUserDTO.email());
//
//                verify(breedRepository, never()).findByName(missingPetDTO.petName());
//
//                verify(petRepository, never()).save(pet);
//
//                verify(missingRepository, times(1)).save(missing);
//            }
//
//            @Test
//            @DisplayName("펫이 존재하지 않는 경우")
//            void notExistPet() {
//                //given
//                User notExistsPetUser = new User(oauthUserDTO.email());
//
//                //when
//                given(userRepository.findByEmail(oauthUserDTO.email()))
//                    .willReturn(Optional.of(notExistsPetUser));
//
//                given(missingRepository.save(missing))
//                    .willReturn(missing);
//
//                given(petRepository.save(pet))
//                    .willReturn(pet);
//
//                given(breedRepository.findByName(missingPetDTO.petBreed()))
//                    .willReturn(Optional.ofNullable(breed));
//
//                given(kakaoMapService.getRegionCodeFromKakao(location))
//                    .willReturn(regionCode);
//                //then
//                missingService.registerMissingPet(oauthUserDTO, missingPetDTO);
//
//                verify(userRepository, times(1))
//                    .findByEmail(oauthUserDTO.email());
//
//                verify(breedRepository, times(1))
//                    .findByName(missingPetDTO.petBreed());
//
//                verify(petRepository, times(1))
//                    .save(pet);
//
//                verify(missingRepository, times(1))
//                    .save(missing);
//            }
//        }
//
//        @Nested
//        @DisplayName("registerMissingPet 400 Bad Request")
//        class registerMissingPet_400 {
//
//            @ParameterizedTest
//            @DisplayName("개월수가 0이하인 값이 입력된 경우")
//            @ValueSource(longs = {-100L, -1L, 0L})
//            void underAgeZero(long petBirthMonth) {
//                //given
//                User user = new User(oauthUserDTO.email());
//
//                missingPetDTO = new MissingPetRequestDTO(
//                    "testPetName",
//                    "testPetGender",
//                    petBirthMonth,
//                    "testPetSpecies",
//                    LocalDateTime.of(2024, 9, 25, 13, 19, 33),
//                    35.17F,
//                    126.90F,
//                    "testPetFeature",
//                    true
//                );
//
//                //when
//                given(userRepository.findByEmail(oauthUserDTO.email()))
//                    .willReturn(Optional.of(user));
//
//                given(breedRepository.findByName(missingPetDTO.petBreed()))
//                    .willReturn(Optional.ofNullable(breed));
//
//                //then
//                CustomException thrown = assertThrows(
//                    CustomException.class,
//                    () -> missingService.registerMissingPet(oauthUserDTO, missingPetDTO)
//                );
//
//                assertAll(
//                    () -> assertEquals(thrown.getStatus(), BAD_REQUEST),
//                    () -> assertEquals(thrown.getMessage(), INVALID_PET_MONTH)
//                );
//            }
//
//            @ParameterizedTest
//            @DisplayName("시간이 현재보다 늦는 경우")
//            @MethodSource("lateMoreThanNowMethodSource")
//            void lateMoreThanNow(LocalDateTime lostTime) {
//                //given
//                User user = new User(oauthUserDTO.email());
//
//                missingPetDTO = new MissingPetRequestDTO(
//                    "testPetName",
//                    "testPetGender",
//                    1L,
//                    "testPetSpecies",
//                    lostTime,
//                    35.17F,
//                    126.90F,
//                    "testPetFeature",
//                    true
//                );
//
//                //when
//                given(userRepository.findByEmail(oauthUserDTO.email()))
//                    .willReturn(Optional.of(user));
//
//                given(breedRepository.findByName(missingPetDTO.petBreed()))
//                    .willReturn(Optional.ofNullable(breed));
//
//                //then
//                CustomException thrown = assertThrows(
//                    CustomException.class,
//                    () -> missingService.registerMissingPet(oauthUserDTO, missingPetDTO)
//                );
//
//                assertAll(
//                    () -> assertEquals(thrown.getStatus(), BAD_REQUEST),
//                    () -> assertEquals(thrown.getMessage(), INVALID_DATE)
//                );
//            }
//
//            private static Stream<LocalDateTime> lateMoreThanNowMethodSource() {
//                return Stream.of(
//                    LocalDateTime.now().plusHours(1L).truncatedTo(SECONDS),
//                    LocalDateTime.now().plusMinutes(10L).truncatedTo(SECONDS)
//                );
//            }
//
//            @ParameterizedTest
//            @DisplayName("위도와 경도가 잘못된 값으로 입력된 경우")
//            @MethodSource("incorrectRangeOfLatitudeAndLongitudeMethodSource")
//            void incorrectRangeOfLatitudeAndLongitude(float lostLatitude, float lostLongitude) {
//                // given
//                User user = new User(oauthUserDTO.email());
//
//                missingPetDTO = new MissingPetRequestDTO(
//                    "testPetName",
//                    "testPetGender",
//                    1L,
//                    "testPetSpecies",
//                    LocalDateTime.of(2024, 9, 25, 13, 19, 33),
//                    lostLatitude,
//                    lostLongitude,
//                    "testPetFeature",
//                    true
//                );
//
//                //when
//                given(userRepository.findByEmail(oauthUserDTO.email()))
//                    .willReturn(Optional.of(user));
//
//                given(breedRepository.findByName(missingPetDTO.petBreed()))
//                    .willReturn(Optional.of(breed));
//
//                //then
//                CustomException thrown = assertThrows(
//                    CustomException.class,
//                    () -> missingService.registerMissingPet(oauthUserDTO, missingPetDTO)
//                );
//
//                assertAll(
//                    () -> assertEquals(thrown.getStatus(), BAD_REQUEST),
//                    () -> assertEquals(thrown.getMessage(), INVALID_LOCATION)
//                );
//
//            }
//
//            private static Stream<Arguments> incorrectRangeOfLatitudeAndLongitudeMethodSource() {
//                return Stream.of(
//                    Arguments.of(-91, 0),
//                    Arguments.of(91, 0),
//                    Arguments.of(0, -181),
//                    Arguments.of(0, 181),
//                    Arguments.of(-91, 181),
//                    Arguments.of(-90.00001F, 0)
//                );
//            }
//        }
//
//        @Test
//        @DisplayName("유효하지 않는 사용자의 경우")
//        void invalidUser() {
//            //when
//            given(userRepository.findByEmail(oauthUserDTO.email()))
//                .willThrow(invalidUserException());
//
//            //then
//            CustomException thrown = assertThrows(
//                CustomException.class,
//                () -> missingService.registerMissingPet(oauthUserDTO, missingPetDTO)
//            );
//
//            assertAll(
//                () -> assertEquals(thrown.getStatus(), UNAUTHORIZED),
//                () -> assertEquals(thrown.getMessage(), INVALID_USER)
//            );
//        }
//
//        @Test
//        @DisplayName("유효하지 않은 종의 경우")
//        void invalidBreed() {
//            //given
//            User user = new User(oauthUserDTO.email());
//
//            //when
//            given(userRepository.findByEmail(oauthUserDTO.email()))
//                .willReturn(Optional.of(user));
//
//            given(breedRepository.findByName(missingPetDTO.petBreed()))
//                .willThrow(breedNotFoundException());
//
//            //then
//            CustomException thrown = assertThrows(
//                CustomException.class,
//                () -> missingService.registerMissingPet(oauthUserDTO, missingPetDTO)
//            );
//
//            assertAll(
//                () -> assertEquals(thrown.getStatus(), NOT_FOUND),
//                () -> assertEquals(thrown.getMessage(), BREED_NOT_FOUND)
//            );
//        }
//    }
//
//    @Nested
//    @DisplayName("[같이 찾기] getMissingPetsNearBy 테스트")
//    class getMissingPetsNearByTest {
//
//        @Test
//        @DisplayName("getMissingPetsNearBy_200")
//        void getMissingPetsNearBy_200() {
//            //given
//            float latitude = 35.12F;
//            float longitude = 126.90F;
//            long regionCode = 1L;
//
//            Pet pet1 = spy(new Pet(
//                "test-name-1",
//                1L,
//                new Breed(),
//                true
//            ));
//
//            Pet pet2 = spy(new Pet(
//                "test-name-2",
//                2L,
//                new Breed(),
//                false
//            ));
//
//            Pet pet3 = spy(new Pet(
//                "test-name-3",
//                3L,
//                new Breed(),
//                false
//            ));
//
//            List<Missing> missingList = List.of(
//                new Missing(
//                    pet1,
//                    true,
//                    LocalDateTime.now(),
//                    new Location(32.5F, 80.0F),
//                    regionCode,
//                    "test-description-1"
//                ),
//                new Missing(
//                    pet2,
//                    true,
//                    LocalDateTime.now().minus(1, HOURS),
//                    new Location(33.5F, 81.0F),
//                    regionCode,
//                    "test-description-2"
//                ),
//                new Missing(
//                    pet3,
//                    false,
//                    LocalDateTime.now().minus(1, MINUTES),
//                    new Location(33.0F, 126.0F),
//                    regionCode,
//                    "test-description-3"
//                )
//            );
//
//            List<MissingPetNearByResponseDTO> expects = List.of(
//                new MissingPetNearByResponseDTO(
//                    1L,
//                    32.5F,
//                    80.0F,
//                    "pet-image-url"
//                ),
//                new MissingPetNearByResponseDTO(
//                    2L,
//                    33.5F,
//                    81.0F,
//                    "pet-image-url"
//                )
//            );
//
//            //when
//            when(kakaoMapService.getRegionCodeFromKakao(new Location(latitude, longitude)))
//                .thenReturn(regionCode);
//
//            when(missingRepository.findAllByRegionCode(regionCode))
//                .thenReturn(missingList);
//
//            when(pet1.getId()).thenReturn(1L);
//            when(pet2.getId()).thenReturn(2L);
//
//            //then
//            List<MissingPetNearByResponseDTO> result = missingService.getMissingPetsNearBy(latitude,
//                longitude);
//            assertEquals(expects, result);
//        }
//    }
//
//    @Nested
//    @DisplayName("[같이 찾기] getMissingPetDetailById 테스트")
//    class getMissingPetDetatilByIdTest {
//
//        private final long missingId = 1L;
//
//        @Test
//        @DisplayName("getMissingPetDetailById_200")
//        void getMissingPetDetailById_200() {
//            // given
//            Missing expectMissing = new Missing(
//                new Pet(
//                    "test-pet-name",
//                    1L,
//                    new Breed("test-breed-name"),
//                    true
//                ),
//                true,
//                LocalDateTime.now(),
//                new Location(10.0F, 15.0F),
//                15,
//                "test-description-1"
//            );
//            MissingPetDetailResponseDTO expect = new MissingPetDetailResponseDTO(
//                "test-pet-name",
//                "test-breed-name",
//                1L,
//                10.0F,
//                15.0F,
//                "test-description-1",
//                // TODO 실제 펫의 image-url로 변경하도록 수정 필요
//                List.of("pet-image-url")
//            );
//
//            // when
//            when(missingRepository.findById(missingId))
//                .thenReturn(Optional.of(expectMissing));
//
//            // then
//            MissingPetDetailResponseDTO result = missingService.getMissingPetDetailByMissingId(
//                missingId);
//            assertEquals(expect, result);
//
//            verify(missingRepository, times(1))
//                .findById(missingId);
//        }
//
//        @Test
//        @DisplayName("getMissingPetDetailById_404")
//        void getMissingPetDetailById_404() {
//            // when
//            when(missingRepository.findById(missingId))
//                .thenThrow(CustomException.missingNotFound());
//
//            // then
//            CustomException thrown = assertThrows(
//                CustomException.class,
//                () -> missingService.getMissingPetDetailByMissingId(missingId)
//            );
//
//            assertAll(
//                () -> assertEquals(thrown.getStatus(), NOT_FOUND),
//                () -> assertEquals(thrown.getMessage(), MISSING_NOT_FOUND)
//            );
//        }
//    }
}