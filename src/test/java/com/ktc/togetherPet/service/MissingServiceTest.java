package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.MISSING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.exception.ErrorMessage;
import com.ktc.togetherPet.model.dto.missing.MissingPetDetailResponseDTO;
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
import java.util.Optional;
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

    @Nested
    @DisplayName("실종의 자세한 정보 가져오기 테스트/getMissingPetDetailByMissingId")
    class 실종의_자세한_정보_가져오기 {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            long missingId = 1L;
            Missing missing = new Missing(
                new Pet(
                    "testPetName",
                    1L,
                    new Breed("testBreedName"),
                    true
                ),
                true,
                LocalDateTime.of(2024, 10, 11, 4, 8, 22),
                new Location(15.0D, 15.0D),
                3,
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

        @Test
        @DisplayName("실종 아이디가 잘못된 경우")
        void 실종_아이디가_잘못된_경우() {
            // given
            long missingId = 1L;

            // when
            when(missingRepository.findById(missingId))
                .thenThrow(CustomException.missingNotFound());

            CustomException thrown = assertThrows(
                CustomException.class,
                () -> missingService.getMissingPetDetailByMissingId(missingId)
            );

            // then
            assertEquals(thrown.getErrorMessage(), ErrorMessage.MISSING_NOT_FOUND);

            verify(missingRepository, times(1))
                .findById(missingId);

            verify(imageService, never())
                .getImageUrl(missingId, MISSING);
        }
    }
}