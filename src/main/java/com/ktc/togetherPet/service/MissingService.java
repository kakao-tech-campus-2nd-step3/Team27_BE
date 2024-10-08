package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.MISSING;
import static com.ktc.togetherPet.model.entity.ImageRelation.ImageEntityType.REPORT;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.missing.MissingPetDetailResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetNearByResponseDTO;
import com.ktc.togetherPet.model.dto.missing.MissingPetRequestDTO;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.report.ReportResponseDTO;
import com.ktc.togetherPet.model.entity.Missing;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.Report;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.BreedRepository;
import com.ktc.togetherPet.repository.MissingRepository;
import com.ktc.togetherPet.repository.PetRepository;
import com.ktc.togetherPet.repository.ReportRepository;
import com.ktc.togetherPet.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissingService {

    private final MissingRepository missingRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final BreedRepository breedRepository;
    private final KakaoMapService kakaoMapService;
    private final ReportRepository reportRepository;
    private final ImageService imageService;

    public void registerMissingPet(OauthUserDTO oauthUserDTO, MissingPetRequestDTO missingPetDTO) {
        User user = userRepository.findByEmail(oauthUserDTO.email())
            .orElseThrow(CustomException::invalidUserException);

        Pet pet = Optional.ofNullable(user.getPet())
            .orElseGet(
                () -> petRepository.save(
                    new Pet(
                        missingPetDTO.petName(),
                        missingPetDTO.birthMonth(),
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
                missing.getPet().getId(),
                missing.getLocation().getLatitude(),
                missing.getLocation().getLongitude(),
                imageService.getImageUrl(missing.getId(), MISSING).getFirst()
            )).toList();
    }

    public MissingPetDetailResponseDTO getMissingPetDetailByMissingId(long missingId) {
        Missing missing = missingRepository.findById(missingId)
            .orElseThrow(CustomException::missingNotFound);

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

//    public ReportDetailResponseDTO getReportDetail(long reportId) {
//        Report report = reportRepository.findById(reportId)
//            .orElseThrow(CustomException::reportNotFoundException);
//
//        return new ReportDetailResponseDTO(
//            report.getUser().getName(),
//            report.getDescription(),
//            report.getLocation().getLatitude(),
//            report.getLocation().getLongitude(),
//            report.getTimeStamp()
//        );
//    }
}
