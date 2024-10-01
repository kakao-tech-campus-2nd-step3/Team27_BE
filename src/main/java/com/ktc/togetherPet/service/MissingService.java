package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.missing.MissingPetDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetDetailDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetNearByDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
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
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MissingService {

    private final MissingRepository missingRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final BreedRepository breedRepository;
    private final KakaoMapService kakaoMapService;

    public MissingService(
        MissingRepository missingRepository,
        UserRepository userRepository,
        PetRepository petRepository,
        BreedRepository breedRepository,
        KakaoMapService kakaoMapService
    ) {
        this.missingRepository = missingRepository;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.breedRepository = breedRepository;
        this.kakaoMapService = kakaoMapService;
    }

    public void registerMissingPet(OauthUserDTO oauthUserDTO, MissingPetDTO missingPetDTO) {
        User user = userRepository.findByEmail(oauthUserDTO.email())
            .orElseThrow(CustomException::invalidUserException);

        Pet pet = Optional.ofNullable(user.getPet())
            .orElseGet(
                () -> petRepository.save(
                    new Pet(
                        missingPetDTO.petName(),
                        new BirthMonth(missingPetDTO.birthMonth()),
                        breedRepository.findByName(missingPetDTO.petBreed())
                            .orElseThrow(CustomException::breedNotFoundException),
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
                new DateTime(missingPetDTO.lostTime()),
                location,
                kakaoMapService.getRegionCodeFromKakao(location),
                missingPetDTO.description()
            )
        );
    }

    public List<MissingPetNearByDTO> getMissingPetsNearBy(float latitude, float longitude) {
        long regionCode = kakaoMapService.getRegionCodeFromKakao(new Location(latitude, longitude));

        return missingRepository.findAllByRegionCode(regionCode)
            .stream()
            .filter(Missing::isMissing)
            .map(missing -> new MissingPetNearByDTO(
                missing.getPet().getId(),
                missing.getLocation().getLatitude(),
                missing.getLocation().getLongitude(),
                //TODO pet-image-url을 실제 경로로 받아오도록 수정해야함.
                "pet-image-url"
            )).toList();
    }

    public MissingPetDetailDTO getMissingPetDetail(long missingId) {
        Missing missing = missingRepository.findById(missingId)
            .orElseThrow(CustomException::missingNotFound);

        Pet pet = missing.getPet();

        return new MissingPetDetailDTO(
            pet.getName(),
            pet.getBreed().getName(),
            pet.getBirthMonth().getBirthMonth(),
            missing.getLocation().getLatitude(),
            missing.getLocation().getLongitude(),
            missing.getDescription(),
            //TODO 여기에 pet-image-url 리스트를 받아오도록 변경해야함
            List.of("pet-image-url")
        );
    }
}
