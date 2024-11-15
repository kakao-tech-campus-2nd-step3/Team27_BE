package com.ktc.togetherPet.service;

import static com.ktc.togetherPet.exception.CustomException.walkNotFoundException;

import com.ktc.togetherPet.exception.CustomException;
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
import com.ktc.togetherPet.model.entity.WalkStatistic;
import com.ktc.togetherPet.repository.WalkRepository;
import com.ktc.togetherPet.util.WalkCalculator;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
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
    private final WalkStatisticService walkStatisticService;

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
        walkStatisticService.saveOrUpdateWalkStatistic(walk);
        pathService.saveAll(walkRequestDTO.locationList(), walk);

        CalorieResponseDTO calorieResponseDTO = new CalorieResponseDTO(
            WalkCalculator.calculateCalorie(walkRequestDTO.totalWalkDistance())
        );

        return calorieResponseDTO;
    }

    public WalkResponseDTO getWalkInformation(OauthUserDTO oauthUserDTO) {
        User user = userService.findUserByEmail(oauthUserDTO.email());

        List<Walk> walkList = walkRepository.getWalksByDate(user.getPet().getId(), LocalDateTime.now().toLocalDate().atStartOfDay(), LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());

        if(walkList.isEmpty()) {
            WalkInformationDTO walkInformation = new WalkInformationDTO(
                0L,0.0,0.0,0.0,0.0,0.0
            );
            return new WalkResponseDTO(0, walkInformation);
        }

        WalkStatistic walkStatistic = walkStatisticService.getWalkStatisticByPetId(walkList.get(0).getPet().getId());

        WalkInformationDTO walkInformationDTO = new WalkInformationDTO(
            (long)walkList.size(),
            (double)walkList.size() / walkStatistic.getWalkDay(),
            walkList.stream().mapToDouble(Walk::getWalkTime).sum(),
            (double)walkStatistic.getTotalWalkTime() / walkStatistic.getWalkCount(),
            walkList.stream().mapToDouble(Walk::getDistance).sum(),
            (double)walkStatistic.getTotalDistance() / walkStatistic.getWalkCount()
        );

        int flagValue = WalkCalculator.calculateFlag(walkInformationDTO);

        return new WalkResponseDTO(flagValue, walkInformationDTO);
    }

    public List<WalkPathByDateResponseDTO> getWalkPathByDate(OauthUserDTO oauthUserDTO, LocalDate date) {
        User user = userService.findUserByEmail(oauthUserDTO.email());

        List<Walk> walkList = walkRepository.getWalksByDate(user.getPet().getId(), date.atStartOfDay(), date.plusDays(1).atStartOfDay());

        if(walkList.isEmpty()) {
            throw walkNotFoundException();
        }

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
