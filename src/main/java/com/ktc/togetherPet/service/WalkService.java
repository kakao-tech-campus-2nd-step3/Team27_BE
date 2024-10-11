package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.dto.oauth.OauthUserDTO;
import com.ktc.togetherPet.model.dto.walk.CalorieResponseDTO;
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

        List<Walk> walk = walkRepository.findByPet(user.getPet());

        //todo: 평균 계산하는 로직 추가
        //todo: 평균에 따라서 플래그 계산하는 로직추가 (WalkCalculator.calculateFlag)

        //todo: 우선 더미데이터 삽입.. 로직 구현 후 삭제
        return new WalkResponseDTO(1,1,1,1, 1, 1, 1);
    }
}
