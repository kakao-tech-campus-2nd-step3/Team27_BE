package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.MISSING;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.missing.MissingPetDetailResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetNearByResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetRequestDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.MissingRepository;
import com.ktc.togetherPet.repository.PetRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissingService {

    private final MissingRepository missingRepository;
    private final UserService userService;
    private final PetRepository petRepository;
    private final BreedService breedService;
    private final KakaoMapService kakaoMapService;
    private final ImageService imageService;

    public void registerMissingPet(OauthUserDTO oauthUserDTO, MissingPetRequestDTO missingPetDTO) {
        User user = userService.findUserByEmail(oauthUserDTO.email());

        Pet pet = Optional.ofNullable(user.getPet())
            .orElseGet(
                () -> petRepository.save(
                    new Pet(
                        missingPetDTO.petName(),
                        missingPetDTO.birthMonth(),
                        breedService.findBreedByName(missingPetDTO.petBreed()),
                        missingPetDTO.isNeutering()
                    )
                )
            );

        Location location = new Location(
            missingPetDTO.latitude(),
            missingPetDTO.longitude()
        );

        missingRepository.save(
            new Missing(
                pet,
                true,
                missingPetDTO.lostTime(),
                location,
                kakaoMapService.getRegionCodeFromKakao(location),
                missingPetDTO.description()
            )
        );
    }

    public List<MissingPetNearByResponseDTO> getMissingPetsNearBy(
        double latitude,
        double longitude
    ) {
        long regionCode = kakaoMapService.getRegionCodeFromKakao(new Location(latitude, longitude));

        return missingRepository.findAllByRegionCode(regionCode)
            .stream()
            .filter(Missing::isMissing)
            .map(missing -> new MissingPetNearByResponseDTO(
                missing.getId(),
                missing.getPet().getId(),
                missing.getLocation().getLatitude(),
                missing.getLocation().getLongitude(),
                imageService.getRepresentativeImageById(MISSING, missing.getId())
            )).toList();
    }

    public MissingPetDetailResponseDTO getMissingPetDetailByMissingId(long missingId) {
        Missing missing = findByMissingId(missingId);
        Pet pet = missing.getPet();

        return new MissingPetDetailResponseDTO(
            pet.getName(),
            pet.getBreed().getName(),
            pet.getBirthMonth(),
            missing.getLocation().getLatitude(),
            missing.getLocation().getLongitude(),
            missing.getDescription(),
            imageService.getImageUrl(missingId, MISSING)
        );
    }

    public Missing findByMissingId(long missingId) {
        return missingRepository.findById(missingId)
            .orElseThrow(CustomException::missingNotFound);
    }
}
