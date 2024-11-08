package com.ktc.togetherPet.service;

import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.walk.CalorieResponseDTO;
import com.ktc.togetherPet.model.dto.walk.WalkInformationDTO;
import com.ktc.togetherPet.model.dto.walk.WalkPathByDateResponseDTO;
import com.ktc.togetherPet.model.dto.walk.WalkRequestDTO;
import com.ktc.togetherPet.model.dto.walk.WalkResponseDTO;
import com.ktc.togetherPet.model.entity.Path;
import com.ktc.togetherPet.model.entity.Pet;
import com.ktc.togetherPet.model.entity.User;
import com.ktc.togetherPet.model.entity.Walk;
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
    private final UserService userService;
    private final PathService pathService;

    @Transactional
    public CalorieResponseDTO createWalk(OauthUserDTO oauthUserDTO, WalkRequestDTO walkRequestDTO) {
        User user = userService.findUserByEmail(oauthUserDTO.email());

        Pet pet = petService.findPetById(user.getPet().getId());

        Walk walk = new Walk(
            pet,
            walkRequestDTO.totalWalkDistance(),
            LocalDateTime.now(),
            walkRequestDTO.totalWalkTime()
        );

        walkRepository.save(walk);

        List<Path> paths = pathService.createPathList(walkRequestDTO.locationList(), walk);
        pathService.saveAll(paths);

        CalorieResponseDTO calorieResponseDTO = new CalorieResponseDTO(
            WalkCalculator.calculateCalorie(walkRequestDTO.totalWalkDistance())
        );

        return calorieResponseDTO;
    }

    public WalkResponseDTO getWalkInformation(OauthUserDTO oauthUserDTO) {
        User user = userService.findUserByEmail(oauthUserDTO.email());

        /** deprecated
        Long todayWalkCount = walkRepository.getTodayWalkCount(user.getPet().getId(), LocalDateTime.now().toLocalDate().atStartOfDay(), LocalDateTime.now());
        Double averageWalkCount = walkRepository.getAverageWalkCount(user.getPet().getId()).orElse(0.0);
        Double todayWalkTime = walkRepository.getTodayWalkTime(user.getPet().getId(), LocalDateTime.now().toLocalDate().atStartOfDay(), LocalDateTime.now()).orElse(0.0);
        Double averageWalkTime = walkRepository.getAv ㅁerageWalkTime(user.getPet().getId()).orElse(0.0);
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
         **/

        WalkInformationDTO walkInformation = walkRepository.getWalkStatistics(user.getPet().getId());

        int flagValue = WalkCalculator.calculateFlag(walkInformation);

        return new WalkResponseDTO(flagValue, walkInformation);
    }

    public List<WalkPathByDateResponseDTO> getWalkPathByDate(OauthUserDTO oauthUserDTO, LocalDateTime date) {
        User user = userService.findUserByEmail(oauthUserDTO.email());

        List<Walk> walkList = walkRepository.getWalksByDate(user.getPet().getId(), date.toLocalDate().atStartOfDay(), date.toLocalDate().plusDays(1).atStartOfDay());

        return walkList
            .stream()
            .map(walk -> new WalkPathByDateResponseDTO(
                pathService.findPathByWalkId(walk.getId()),
                walk.getDistance(),
                walk.getWalkTime(),
                walk.getWalkDate(),
                walk.getWalkDate().plusSeconds(walk.getWalkTime() / 1000)
            )).toList();
    }
}
