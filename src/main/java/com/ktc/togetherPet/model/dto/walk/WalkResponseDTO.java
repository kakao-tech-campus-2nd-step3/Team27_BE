package com.ktc.togetherPet.model.dto.walk;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record WalkResponseDTO(
    int flagValue,
    int totalCount,
    int avgWalkCount,
    float totalWalkDistance,
    float avgWalkDistance,
    long totalWalkTime,
    long avgWalkTime
) {

}
