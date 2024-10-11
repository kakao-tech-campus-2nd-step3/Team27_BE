package com.ktc.togetherPet.model.dto.walk;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public record WalkRequestDTO(
    float totalWalkDistance,
    long totalWalkTime,
    List<LocationDTO> locationList //todo: 이 부분은 논의가 필요함
) {

}
