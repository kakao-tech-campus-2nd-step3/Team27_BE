package com.ktc.togetherPet.service;

import com.ktc.togetherPet.exception.CustomException;
import com.ktc.togetherPet.model.entity.Region;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;
    private final KakaoMapService kakaoMapService;

    public Region findByLocation(Location location) {
        return findById(kakaoMapService.getRegionCodeFromKakao(location));
    }

    private Region findById(Long id) {
        return regionRepository.findById(id)
            .orElseThrow(CustomException::regionNotFoundException);
    }
}
