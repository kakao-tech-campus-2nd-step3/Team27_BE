package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.exception.ErrorMessage.MISSING_NOT_FOUND;
import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.MISSING;
import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.PET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.missing.MissingPetDetailResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetNearByResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetRequestDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.entity.Breed;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.Region;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.MissingRepository;
import com.ktc.togetherPet.repository.PetRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
    private RegionService regionService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private MissingService missingService;

    private List<Pet> givenPet;

    private Region givenRegion;

    @BeforeEach
    void setUp() {
        List<Breed> givenBreed = List.of(
            new Breed("testPetBreed1"),
            new Breed("testPetBreed2")
        );

        givenPet = List.of(
            spy(new Pet(
                "testPetName1",
                1L,
                givenBreed.getFirst(),
                true
            )),
            spy(new Pet(
                "testPetName2",
                2L,
                givenBreed.get(1),
                true
            )),
            spy(new Pet(
                "testPetName3",
                3L,
                givenBreed.getFirst(),
                false
            ))
        );

        givenRegion = new Region(
            1L,
            "testProvince",
            "testDistrict",
            "testNeighborhood"
        );
    }

    @Test
    @DisplayName("실종 등록 테스트/registerMissingPet")
    void 실종_등록() {
        // given
        OauthUserDTO oauthUserDTO = new OauthUserDTO("test@email.com");
        MissingPetRequestDTO missingPetRequestDTO = new MissingPetRequestDTO(
            "testPetName1",
            "testPetGender",
            1L,
            "testPetBreed1",
            LocalDateTime.of(2024, 10, 11, 3, 2, 22),
            15.0D,
            15.0D,
            "testDescription",
            true
        );

        User expectUser = new User(oauthUserDTO.email());

        Location location = new Location(
            missingPetRequestDTO.latitude(),
            missingPetRequestDTO.longitude()
        );

        expectUser.setPet(givenPet.getFirst());

        // when
        when(userService.findUserByEmail(oauthUserDTO.email()))
            .thenReturn(expectUser);

        when(regionService.findByLocation(location))
            .thenReturn(givenRegion);

        // then
        missingService.registerMissingPet(oauthUserDTO, missingPetRequestDTO);

        verify(userService, times(1))
            .findUserByEmail(oauthUserDTO.email());

        verify(petRepository, never())
            .save(givenPet.getFirst());

        verify(breedService, never())
            .findBreedByName(missingPetRequestDTO.petBreed());

        verify(missingRepository, times(1))
            .save(new Missing(
                givenPet.getFirst(),
                true,
                missingPetRequestDTO.lostTime(),
                location,
                givenRegion,
                missingPetRequestDTO.description()
            ));
    }

    @Test
    @DisplayName("근처의 실종 동물 정보 가져오기 테스트/getMissingPetsNearBy")
    void 성공() {
        // given
        double latitude = 15.0D;
        double longitude = 15.0D;

        List<Missing> expectMissing = List.of(
            spy(new Missing(
                givenPet.getFirst(),
                true,
                LocalDateTime.of(2024, 10, 11, 3, 49, 44),
                new Location(15.1D, 15.1D),
                givenRegion,
                "testDescription1"
            )),
            spy(new Missing(
                givenPet.get(1),
                true,
                LocalDateTime.of(2024, 10, 1, 3, 49, 44),
                new Location(15.2D, 15.2D),
                givenRegion,
                "testDescription2"
            ))
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
        when(regionService.findByLocation(new Location(latitude, longitude)))
            .thenReturn(givenRegion);

        when(missingRepository.findAllByRegionAndIsMissingIsTrue(givenRegion))
            .thenReturn(expectMissing);

        when(givenPet.getFirst().getId())
            .thenReturn(1L);

        when(givenPet.get(1).getId())
            .thenReturn(2L);

        when(expectMissing.get(0).getId())
            .thenReturn(1L);

        when(expectMissing.get(1).getId())
            .thenReturn(2L);

        when(imageService.getRepresentativeImageById(PET, givenPet.getFirst().getId()))
            .thenReturn(expectImageUrl1);

        when(imageService.getRepresentativeImageById(PET, givenPet.get(1).getId()))
            .thenReturn(expectImageUrl2);

        // then
        List<MissingPetNearByResponseDTO> actual = missingService.getMissingPetsNearBy(latitude,
            longitude);

        assertEquals(actual, expect);

        verify(regionService, times(1))
            .findByLocation(new Location(latitude, longitude));

        verify(missingRepository, times(1))
            .findAllByRegionAndIsMissingIsTrue(givenRegion);

        verify(imageService, times(1))
            .getRepresentativeImageById(PET, expectMissing.get(0).getId());

        verify(imageService, times(1))
            .getRepresentativeImageById(PET, expectMissing.get(1).getId());
    }

    @Test
    @DisplayName("실종의 자세한 정보 가져오기 테스트/getMissingPetDetailByMissingId")
    void 실종의_자세한_정보_가져오기() {
        // given
        long missingId = 1L;
        Missing missing = new Missing(
            givenPet.getFirst(),
            true,
            LocalDateTime.of(2024, 10, 11, 4, 8, 22),
            new Location(15.0D, 15.0D),
            givenRegion,
            "testDescription"
        );
        List<String> expectImageUrls = List.of(
            "https://together-pet/images/test-image-1.jpeg",
            "https://together-pet/images/test-image-2.jpeg",
            "https://together-pet/images/test-image-3.jpeg"
        );
        MissingPetDetailResponseDTO expect = new MissingPetDetailResponseDTO(
            missing.getPet().getName(),
            missing.getPet().getBreed().getName(),
            missing.getPet().getBirthMonth(),
            missing.getLocation().getLatitude(),
            missing.getLocation().getLongitude(),
            missing.getDescription(),
            expectImageUrls
        );

        // when
        when(missingRepository.findById(missingId))
            .thenReturn(Optional.of(missing));

        when(imageService.getImageUrl(missingId, MISSING))
            .thenReturn(expectImageUrls);

        // then
        MissingPetDetailResponseDTO actual = missingService.getMissingPetDetailByMissingId(
            missingId);

        assertEquals(actual, expect);

        verify(missingRepository, times(1))
            .findById(missingId);

        verify(imageService, times(1))
            .getImageUrl(missingId, MISSING);
    }

    @Nested
    @DisplayName("실종 아이디를 기반으로 실종 엔티티 찾기 테스트/findByMissingId")
    class 실종_아이디를_기반으로_실종_엔티티_찾기 {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            long missingId = 1L;

            Missing expectMissing = new Missing(
                givenPet.getFirst(),
                true,
                LocalDateTime.of(2024, 10, 11, 5, 27, 22),
                new Location(15.0D, 15.0D),
                givenRegion,
                "testDescription"
            );

            // when
            when(missingRepository.findById(missingId))
                .thenReturn(Optional.of(expectMissing));

            // then
            assertEquals(expectMissing, missingService.findByMissingId(missingId));
        }

        @Test
        @DisplayName("실패")
        void 실패() {
            // given
            long missingId = 1L;

            // when
            when(missingRepository.findById(missingId))
                .thenReturn(Optional.empty());

            CustomException thrown = assertThrows(
                CustomException.class,
                () -> missingService.getMissingPetDetailByMissingId(missingId)
            );

            // then
            assertEquals(thrown.getErrorMessage(), MISSING_NOT_FOUND);

            verify(missingRepository, times(1))
                .findById(missingId);

            verify(imageService, never())
                .getImageUrl(missingId, MISSING);
        }
    }

    @Nested
    @DisplayName("애완동물을 통해 실종 정보 찾기 테스트/findByPet")
    class 애완동물을_통해_실종_정보_찾기 {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Missing expect = new Missing(
                givenPet.getFirst(),
                true,
                LocalDateTime.of(2024, 10, 11, 6, 21, 22),
                new Location(15.0D, 15.0D),
                givenRegion,
                "testDescription"
            );

            // when
            when(missingRepository.findByPetAndIsMissingIsTrue(givenPet.getFirst()))
                .thenReturn(Optional.of(expect));

            // then
            Missing actual = missingService.findByPet(givenPet.getFirst());

            assertEquals(actual, expect);

            verify(missingRepository, times(1))
                .findByPetAndIsMissingIsTrue(givenPet.getFirst());
        }

        @Test
        @DisplayName("실패")
        void 실패() {
            // when
            when(missingRepository.findByPetAndIsMissingIsTrue(givenPet.getFirst()))
                .thenReturn(Optional.empty());

            // then
            CustomException thrown = assertThrows(
                CustomException.class,
                () -> missingService.findByPet(givenPet.getFirst())
            );

            assertEquals(thrown.getErrorMessage(), MISSING_NOT_FOUND);

            verify(missingRepository, times(1))
                .findByPetAndIsMissingIsTrue(givenPet.getFirst());
        }
    }
}