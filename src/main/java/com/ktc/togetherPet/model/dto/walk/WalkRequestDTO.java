package com.ktc.togetherPet.model.dto.walk;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record WalkRequestDTO(
    double totalWalkDistance,
    long totalWalkTime,
    List<LocationDTO> locationList
) {

}
