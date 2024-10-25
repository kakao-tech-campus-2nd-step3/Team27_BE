package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.walk.CalorieResponseDTO;
import com.ktc.togetherPet.model.dto.walk.WalkInformationDTO;
import com.ktc.togetherPet.model.dto.walk.WalkRequestDTO;
import com.ktc.togetherPet.model.dto.walk.WalkResponseDTO;
import com.ktc.togetherPet.model.entity.Path;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.entity.Walk;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.PathRepository;
import com.ktc.togetherPet.repository.UserRepository;
import com.ktc.togetherPet.repository.WalkRepository;
import com.ktc.togetherPet.util.WalkCalculator;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalkService {

    private final WalkRepository walkRepository;
    private final PetService petService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PathRepository pathRepository;

    @Transactional
    public CalorieResponseDTO createWalk(OauthUserDTO oauthUserDTO, WalkRequestDTO walkRequestDTO) {
        User user = userRepository.findByEmail(oauthUserDTO.email())
            .orElseThrow(CustomException::invalidUserException);

        Pet pet = petService.findPetById(user.getPet().getId());

        Walk walk = new Walk(
            pet,
            walkRequestDTO.totalWalkDistance(),
            LocalDateTime.now(),
            walkRequestDTO.totalWalkTime()
        );

        List<Path> paths = walkRequestDTO.locationList().stream()
                .map(locationDTO -> new Path(new Location(locationDTO.latitude(), locationDTO.longitude()), walk))
                .toList();

        pathRepository.saveAll(paths);
        walkRepository.save(walk);

        CalorieResponseDTO calorieResponseDTO = new CalorieResponseDTO(
            WalkCalculator.calculateCalorie(walkRequestDTO.totalWalkDistance())
        );

        return calorieResponseDTO;
    }

    public WalkResponseDTO getWalkInformation(OauthUserDTO oauthUserDTO) {
        User user = userRepository.findByEmail(oauthUserDTO.email())
            .orElseThrow(CustomException::invalidUserException);

        Long todayWalkCount = walkRepository.getTodayWalkCount(user.getPet().getId(), LocalDateTime.now().toLocalDate().atStartOfDay(), LocalDateTime.now());
        Double averageWalkCount = walkRepository.getAverageWalkCount(user.getPet().getId()).orElse(0.0);
        Double todayWalkTime = walkRepository.getTodayWalkTime(user.getPet().getId(), LocalDateTime.now().toLocalDate().atStartOfDay(), LocalDateTime.now()).orElse(0.0);
        Double averageWalkTime = walkRepository.getAverageWalkTime(user.getPet().getId()).orElse(0.0);
        Double todayWalkDistance = walkRepository.getTodayWalkDistance(user.getPet().getId(), LocalDateTime.now().toLocalDate().atStartOfDay(), LocalDateTime.now()).orElse(0.0);
        Double averageWalkDistance = walkRepository.getAverageWalkDistance(user.getPet().getId()).orElse(0.0);

        WalkInformationDTO walkInformation = new WalkInformationDTO(
            todayWalkCount,
            averageWalkCount,
            todayWalkTime,
            averageWalkTime,
            todayWalkDistance,
            averageWalkDistance
        );

        int flagValue = WalkCalculator.calculateFlag(walkInformation);

        return new WalkResponseDTO(flagValue, walkInformation);
    }
}
