package com.ktc.togetherPet.model.dto.walk;

import java.time.LocalDateTime;
import java.util.List;

public record WalkPathByDateResponseDTO(
    List<LocationDTO> locationList,
    Float WalkDistance,
    Long WalkTime,
    LocalDateTime WalkStartTimePoint,
    LocalDateTime WalkEndTimePoint
) {

}
