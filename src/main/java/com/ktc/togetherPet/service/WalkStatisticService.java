package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.entity.Walk;
import com.ktc.togetherPet.model.entity.WalkStatistic;
import com.ktc.togetherPet.repository.WalkStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalkStatisticService {

    private final WalkStatisticRepository walkStatisticRepository;

    public void saveOrUpdateWalkStatistic(Walk walk) {
        WalkStatistic walkStatistic = walkStatisticRepository.findByPetId(walk.getPet().getId())
            .orElse(new WalkStatistic(walk));

        walkStatistic.updateWalkStatistic(walk.getDistance(), walk.getWalkTime());

        walkStatisticRepository.save(walkStatistic);
    }

    public WalkStatistic getWalkStatisticByPetId(Long petId) {
        return walkStatisticRepository.findByPetId(petId)
            .orElseThrow(CustomException::walkNotFoundException);
    }
}
