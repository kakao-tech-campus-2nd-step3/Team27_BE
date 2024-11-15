package com.ktc.togetherPet.service;

import com.ktc.togetherPet.model.dto.walk.LocationDTO;
import com.ktc.togetherPet.model.entity.Path;
import com.ktc.togetherPet.model.entity.Walk;
import com.ktc.togetherPet.model.vo.Location;
import com.ktc.togetherPet.repository.PathRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathService {

    private final PathRepository pathRepository;

    public void saveAll(List<LocationDTO> locationList, Walk walk) {
        List<Path> paths = locationList.stream()
            .map(locationDTO -> new Path(new Location(locationDTO.latitude(), locationDTO.longitude()), walk))
            .toList();

        pathRepository.saveAll(paths);
    }

    public List<LocationDTO> findPathByWalkId(Long walkId) {
        return pathRepository.findByWalkId(walkId)
            .stream()
            .map(path -> new LocationDTO(path.getLocation().getLatitude(), path.getLocation().getLongitude()))
            .toList();
    }
}
